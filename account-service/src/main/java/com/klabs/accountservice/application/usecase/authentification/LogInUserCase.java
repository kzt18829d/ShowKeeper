package com.klabs.accountservice.application.usecase.authentification;

import com.klabs.accountservice.application.dto.TokenDTO;
import com.klabs.accountservice.application.mapper.AccountMapper;
import com.klabs.accountservice.application.port.output.CachePort;
import com.klabs.accountservice.application.port.output.TokenPort;
import com.klabs.accountservice.domain.model.Account;
import com.klabs.accountservice.domain.model.AuditLog;
import com.klabs.accountservice.domain.repository.AccountRepository;
import com.klabs.accountservice.domain.repository.AuditLogRepository;
import com.klabs.accountservice.domain.service.AccountValidationService;
import com.klabs.accountservice.domain.service.PasswordHashingService;
import com.klabs.accountservice.domain.valueobject.Email;
import com.klabs.accountservice.domain.valueobject.Login;
import com.klabs.accountservice.domain.valueobject.Password;
import com.klabs.accountservice.domain.valueobject.Token;
import com.klabs.accountservice.shared.exception.AccountNotFoundException;
import com.klabs.accountservice.shared.exception.InvalidCredentialsException;
import com.klabs.accountservice.shared.exception.PasswordNotSetException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LogInUserCase {
    private final AccountRepository accountRepository;
    private final AuditLogRepository auditLogRepository;
    private final CachePort cachePort;
    private final PasswordHashingService passwordHashingService;
    private final AccountValidationService accountValidationService;
    private final TokenPort tokenPort;
    private final AccountMapper accountMapper;

    private Account findAccountByIdentifier(String identifier) {
        if (identifier.contains("@")) {
            Email email = new Email(identifier);
            return accountRepository.findByEmail(email).orElseThrow( () ->  new AccountNotFoundException(email));
        } else {
            Login login = new Login(identifier);
            return accountRepository.findByLogin(login).orElseThrow( () -> new AccountNotFoundException(login));
        }
    }

    private void validatePasswordAndAccount(Account account, String plainPassword) {
        if (!account.hasPassword())
            throw new PasswordNotSetException("Password not ser. Please use OAuth login");
        Password password = Password.fromPlainText(plainPassword, passwordHashingService);

        if (!passwordHashingService.matches(password, account.getPassword()))
            throw new InvalidCredentialsException("Invalid credentials");
        accountValidationService.validateCanLogIn(account);
    }

    private TokenDTO generateTokens(Account account) {
        Token accessToken = tokenPort.generateAccessToken(account.getUuid());
        Token refreshToken = tokenPort.generateRefreshToken(account.getUuid());
        cachePort.saveToken(accessToken.getId(), account.getUuid().toString(), Duration.ofMinutes(15));
        cachePort.saveToken(refreshToken.getId(), account.getUuid().toString(), Duration.ofDays(14));
        return TokenDTO.builder()
                .accessToken(accessToken.getValue())
                .refreshToken(refreshToken.getValue())
                .tokenType("Bearer")
                .expiresIn(900)
                .issuedAt(LocalDateTime.now())
                .account(accountMapper.toDTO(account))
                .build();
    }

    private void saveSessionAndAuditLog(Account account, String ipAddress, String userAgent, String sessionID) {
        cachePort.saveSession(sessionID, account.getUuid().toString(), ipAddress, userAgent, Duration.ofDays(14));

        AuditLog auditLog = AuditLog.login(account.getUuid(), ipAddress, userAgent);
        auditLogRepository.save(auditLog);
        account.recordLogIn();
        accountRepository.save(account);
    }

    public TokenDTO execute(String identifier, String password, String ipAddress, String userAgent) {
        Account account = findAccountByIdentifier(identifier);
        validatePasswordAndAccount(account, password);
        TokenDTO tokenDTO = generateTokens(account);

        String sessionID = UUID.randomUUID().toString();
        saveSessionAndAuditLog(account, ipAddress, userAgent, sessionID);
        return tokenDTO;
    }
}
