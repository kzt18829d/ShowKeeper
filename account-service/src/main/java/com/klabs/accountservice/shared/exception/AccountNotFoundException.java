package com.klabs.accountservice.shared.exception;

import java.util.UUID;

public class AccountNotFoundException extends BusinessException {
    public AccountNotFoundException(String message) {
        super(message);
    }

    public AccountNotFoundException(UUID accountUUID) {
        super(String.format("Account with UUID %s not found", accountUUID));
    }

    public AccountNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountNotFoundException(Throwable cause) {
        super(cause);
    }

    public AccountNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public AccountNotFoundException() {
        super();
    }
}
