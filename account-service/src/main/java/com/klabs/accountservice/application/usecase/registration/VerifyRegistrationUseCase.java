package com.klabs.accountservice.application.usecase.registration;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klabs.accountservice.application.dto.TokenDTO;
import com.klabs.accountservice.application.mapper.AccountMapper;
import com.klabs.accountservice.application.port.output.CachePort;
import com.klabs.accountservice.application.port.output.EventPublisherPort;
import com.klabs.accountservice.application.port.output.TokenPort;
import com.klabs.accountservice.domain.event.AccountRegisteredEvent;
import com.klabs.accountservice.domain.model.Account;
import com.klabs.accountservice.domain.repository.AccountRepository;
import com.klabs.accountservice.domain.service.PasswordHashingService;
import com.klabs.accountservice.domain.valueobject.*;
import com.klabs.accountservice.shared.exception.InvalidVerificationCodeException;
import com.klabs.accountservice.shared.exception.VerificationCodeExpiredException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class VerifyRegistrationUseCase {
    private final AccountRepository accountRepository;
    private final CachePort cachePort;
    private final EventPublisherPort eventPublisherPort;
    private final PasswordHashingService passwordHashingService;
    private final TokenPort tokenPort;
    private final AccountMapper accountMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TypeReference<Map<String, String>> typeReference = new TypeReference<Map<String, String>>() {};

    private VerificationCode retrieveAndValidateCode(String registrationID, String inputCode) {
        String cacheKey = "registration:verification:" + registrationID;
        String savedCode = cachePort.getVerificationCode(cacheKey).orElseThrow( () -> new VerificationCodeExpiredException("Verification code expired or not found"));
        Duration ttl = cachePort.getTTL(cacheKey).orElseThrow( () -> new VerificationCodeExpiredException("Verification code expired or fot found (ttl value)"));

        VerificationCode code = VerificationCode.codeOf(savedCode, LocalDateTime.now().plus(ttl));
        if (code.isExpired())
            throw new VerificationCodeExpiredException("Verification code expired");
        if (!code.matches(inputCode))
            throw new InvalidVerificationCodeException("Invalid verification code");
        return code;
    }

    private Account retrieveRegistrationDataAndCreateAccount (String registrationID) {
        String cacheKey = "registration:data:" + registrationID;
        String data = cachePort.getValue(cacheKey).orElseThrow( () -> new IllegalStateException("Registration data not found"));
        try {
            Map<String, String> fromJSON = objectMapper.readValue(data, typeReference);
            Email email = new Email(fromJSON.get("email"));
            Login login = new Login(fromJSON.get("login"));
            Password password = Password.fromHash(fromJSON.get("passwordHash"));

            Account account = Account.createNew(login, email, password);
            account.verifyEmail();
            return account;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void cleanRegistrationCache(String registrationID) {
        cachePort.deleteVerificationCode("registration:verification:" + registrationID);
        cachePort.deleteValue("registration:data:" + registrationID);
    }

    public TokenDTO execute(String registrationID, String verificationCode) {
        retrieveAndValidateCode(registrationID, verificationCode);
        Account account = retrieveRegistrationDataAndCreateAccount(registrationID);
        Account savedAccount = accountRepository.save(account);

        cleanRegistrationCache(registrationID);

        AccountRegisteredEvent event = AccountRegisteredEvent.from(savedAccount);
        eventPublisherPort.publish(event);

        Token accessToken = tokenPort.generateAccessToken(savedAccount.getUuid());
        Token refreshToken = tokenPort.generateRefreshToken(savedAccount.getUuid());

        cachePort.saveToken(accessToken.getId(), savedAccount.getUuid().toString(), Duration.ofMinutes(15));
        cachePort.saveToken(refreshToken.getId(), savedAccount.getUuid().toString(), Duration.ofDays(14));
        return TokenDTO.builder()
                .accessToken(accessToken.getValue())
                .refreshToken(refreshToken.getValue())
                .tokenType("Bearer")
                .expiresIn(900)
                .issuedAt(LocalDateTime.now())
                .account(accountMapper.toDTO(savedAccount))
                .build();
    }
}
