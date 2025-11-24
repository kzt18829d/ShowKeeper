package com.klabs.accountservice.application.usecase.registration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klabs.accountservice.application.dto.RegistrationDTO;
import com.klabs.accountservice.application.port.output.CachePort;
import com.klabs.accountservice.application.port.output.EmailPort;
import com.klabs.accountservice.domain.repository.AccountRepository;
import com.klabs.accountservice.domain.repository.DeletedAccountRepository;
import com.klabs.accountservice.domain.service.AccountValidationService;
import com.klabs.accountservice.domain.service.PasswordHashingService;
import com.klabs.accountservice.domain.valueobject.Email;
import com.klabs.accountservice.domain.valueobject.Login;
import com.klabs.accountservice.domain.valueobject.VerificationCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InitiateRegistrationUseCase {
    private final AccountRepository accountRepository;
    private final DeletedAccountRepository deletedAccountRepository;
    private final CachePort cachePort;
    private final EmailPort emailPort;
    private final AccountValidationService accountValidationService;
    private final PasswordHashingService passwordHashingService;

    private void validateRegistrationData(Email email, Login login) {
        accountValidationService.validateRegistration(email, login);
    }

    private VerificationCode generateAndSaveVerificationCode(String registrationID) {
        VerificationCode code = VerificationCode.generate();
        cachePort.saveVerificationCode("registration:verification:" + registrationID, code.getCode(), Duration.ofMinutes(10));
        return code;
    }

    private void saveRegistrationDataInCache(String registrationID, Email email, Login login, String passwordHash) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> map = Map.of(
                "email", email.getValue(),
                "login", login.getValue(),
                "passwordHash", passwordHash
        );
        try {
            String json = objectMapper.writeValueAsString(map);
            cachePort.saveValue("registration:data:" + registrationID, json, Duration.ofMinutes(10));
            log.info("[SUCCESS] Redis --save --arg-registration:data:{}", registrationID );
        } catch (JsonProcessingException e) {
            log.info("[FAIL] Redis --save --arg-registration:data:{} --withError- {}", registrationID, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public RegistrationDTO execute(String emailStr, String loginStr, String plainPassword) {
        Email email = new Email(emailStr);
        Login login = new Login(loginStr);
        validateRegistrationData(email, login);
        String hash = passwordHashingService.hash(plainPassword);
        String registrationID = UUID.randomUUID().toString();
        VerificationCode code = generateAndSaveVerificationCode(registrationID);
        saveRegistrationDataInCache(registrationID, email, login, hash);
        emailPort.sendVerificationCode(email.getValue(), code.getCode());
        return RegistrationDTO.builder()
                .registrationID(registrationID)
                .email(email.getValue())
                .login(login.getValue())
                .expiresAt(code.getExpiresAt())
                .build();
    }
}
