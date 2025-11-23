package com.klabs.accountservice.application.usecase.validation;

import com.klabs.accountservice.application.dto.AccountDTO;
import com.klabs.accountservice.application.mapper.AccountMapper;
import com.klabs.accountservice.application.port.output.CachePort;
import com.klabs.accountservice.application.port.output.TokenPort;
import com.klabs.accountservice.domain.model.Account;
import com.klabs.accountservice.domain.model.AccountStatus;
import com.klabs.accountservice.domain.repository.AccountRepository;
import com.klabs.accountservice.domain.valueobject.Token;
import com.klabs.accountservice.shared.exception.AccountAlreadyDeletedException;
import com.klabs.accountservice.shared.exception.AccountNotFoundException;
import com.klabs.accountservice.shared.exception.AccountSuspendedException;
import com.klabs.accountservice.shared.exception.TokenExpiredException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ValidateTokenUseCase {

    private final CachePort cachePort;
    private final AccountRepository accountRepository;
    private final TokenPort tokenPort;
    private final AccountMapper accountMapper;

    private Token parseAndValidateToken(String tokenString) {
        return tokenPort.parseAndValidateToken(tokenString);
    }

    private void checkTokenRevocation(String tokenString) {
        if (!cachePort.isTokenValid(tokenString))
            throw new TokenExpiredException("Token has been revoked");
    }

    private Account validateAccountExists(UUID accountUUID) {
        Account account = accountRepository.findByUUID(accountUUID).orElseThrow(() -> new AccountNotFoundException(accountUUID));

        if (!account.getAccountStatus().canBeDeleted())
            throw new AccountAlreadyDeletedException("Account deleted");

        if (account.getAccountStatus() == AccountStatus.SUSPENDED)
            throw new AccountSuspendedException("Account suspend");
        return account;
    }

    public AccountDTO execute(String tokenString) {
        Token token = parseAndValidateToken(tokenString);
        checkTokenRevocation(tokenString);
        Account account = validateAccountExists(tokenPort.extractAccountUUID(tokenString));
        return accountMapper.toDTO(account);
    }
}
