package com.klabs.accountservice.shared.exception;

public class AccountSuspendedException extends BusinessException {
    public AccountSuspendedException(String message) {
        super(message);
    }
}
