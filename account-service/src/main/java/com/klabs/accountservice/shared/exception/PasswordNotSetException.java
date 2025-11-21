package com.klabs.accountservice.shared.exception;

public class PasswordNotSetException extends BusinessException {
    public PasswordNotSetException(String message) {
        super(message);
    }
}
