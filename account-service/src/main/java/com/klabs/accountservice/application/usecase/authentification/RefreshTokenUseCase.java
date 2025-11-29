package com.klabs.accountservice.application.usecase.authentification;

import com.klabs.accountservice.application.dto.TokenDTO;
import com.klabs.accountservice.application.mapper.AccountMapper;
import com.klabs.accountservice.application.port.output.CachePort;
import com.klabs.accountservice.application.port.output.TokenPort;
import com.klabs.accountservice.domain.model.Account;
import com.klabs.accountservice.domain.repository.AccountRepository;
import com.klabs.accountservice.domain.service.AccountValidationService;
import com.klabs.accountservice.domain.valueobject.Token;
import com.klabs.accountservice.shared.exception.AccountNotFoundException;
import com.klabs.accountservice.shared.exception.TokenExpiredException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenUseCase {
    private final AccountRepository accountRepository;
    private final CachePort cachePort;
    private final TokenPort tokenPort;
    private final AccountValidationService accountValidationService;
    private final AccountMapper accountMapper;


    private Token validateAndParseRefreshToken(String refreshTokenString) {
        Token refreshToken = tokenPort.parseAndValidateToken(refreshTokenString);
        if (refreshToken.isExpired())
            throw new TokenExpiredException("Refresh token expired");
        if (!cachePort.isTokenValid(refreshToken.getId()))
            throw new TokenExpiredException("Refresh token has been revoked");
        return refreshToken;
    }

    private Account validateAccountFromToken(UUID accountUUID) {
        Account account = accountRepository.findByUUID(accountUUID).orElseThrow( () -> new AccountNotFoundException(accountUUID));
        accountValidationService.validateCanLogIn(account);
        return account;
    }

    private TokenDTO generateNewAccessToken(Account account, Token oldRefreshToken) {
        Token accessToken = tokenPort.generateAccessToken(account.getUuid());
        cachePort.saveToken(accessToken.getId(), account.getUuid().toString(), Duration.ofMinutes(15));
        return TokenDTO.builder()
                .accessToken(accessToken.getValue())
                .refreshToken(oldRefreshToken.getValue())
                .tokenType("Bearer")
                .expiresIn(900)
                .issuedAt(LocalDateTime.now())
                .account(accountMapper.toDTO(account))
                .build();
    }

    public TokenDTO execute(String refreshTokenString) {
        Token refreshToken = validateAndParseRefreshToken(refreshTokenString);
        UUID accountUUID = refreshToken.getSubject();
        Account account = validateAccountFromToken(accountUUID);
        return generateNewAccessToken(account, refreshToken);
    }
}
