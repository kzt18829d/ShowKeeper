package com.klabs.accountservice.application.usecase.authentification;

import com.klabs.accountservice.application.port.output.CachePort;
import com.klabs.accountservice.application.port.output.TokenPort;
import com.klabs.accountservice.domain.model.AuditLog;
import com.klabs.accountservice.domain.repository.AuditLogRepository;
import com.klabs.accountservice.domain.valueobject.Token;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LogOutUseCase {
    private final CachePort cachePort;
    private final AuditLogRepository auditLogRepository;
    private final TokenPort tokenPort;

    private UUID extractAccountUUIdFromToken(String tokenString) {
        return tokenPort.parseAndValidateToken(tokenString).getSubject();
    }

    private void revokeTokens(String accessTokenString, String refreshTokenString) {
        Token accessToken = tokenPort.parseAndValidateToken(accessTokenString);
        Duration accessTTL = Duration.between(LocalDateTime.now(), accessToken.getExpiresAt());
        cachePort.revokeToken(accessToken.getId(), accessTTL);

        Token refreshToken = tokenPort.parseAndValidateToken(refreshTokenString);
        Duration refreshTTL = Duration.between(LocalDateTime.now(), refreshToken.getExpiresAt());
        cachePort.revokeToken(refreshToken.getId(), refreshTTL);

        String sessionID = refreshToken.getId();
        UUID accountUUID = extractAccountUUIdFromToken(accessTokenString);
        cachePort.deleteSession(sessionID, accountUUID.toString());
    }

    public void execute(String accessToken, String refreshToken, String ipAddress, String userAgent) {
        UUID accountUUID = extractAccountUUIdFromToken(accessToken);
        revokeTokens(accessToken, refreshToken);
        AuditLog auditLog = AuditLog.logout(accountUUID, ipAddress, userAgent);
        auditLogRepository.save(auditLog);
        log.info("LogOut success on {}/{} with account {}", ipAddress, userAgent, accountUUID);
    }
}
