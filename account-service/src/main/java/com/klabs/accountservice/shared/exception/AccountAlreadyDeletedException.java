package com.klabs.accountservice.shared.exception;

public class AccountAlreadyDeletedException extends BusinessException {
    public AccountAlreadyDeletedException(String message) {
        super(message);
    }
}
