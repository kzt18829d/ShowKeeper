package com.klabs.accountservice.shared.exception;

public class EmailNotVerifiedException extends BusinessException {
    public EmailNotVerifiedException(String message) {
        super(message);
    }
}
