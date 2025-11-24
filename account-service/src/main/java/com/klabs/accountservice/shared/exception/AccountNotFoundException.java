package com.klabs.accountservice.shared.exception;

import com.klabs.accountservice.domain.valueobject.Email;
import com.klabs.accountservice.domain.valueobject.Login;

import java.util.UUID;

public class AccountNotFoundException extends BusinessException {
    public AccountNotFoundException(String message) {
        super(message);
    }

    public AccountNotFoundException(UUID accountUUID) {
        super(String.format("Account with UUID %s not found", accountUUID));
    }

    public AccountNotFoundException(Email email) {
        super(String.format("Account with email %s not found", email.getValue()));
    }

    public AccountNotFoundException(Login login) {
        super(String.format("Account with login %s not found", login.getValue()));
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
