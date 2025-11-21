package com.klabs.accountservice.shared.exception;

public class VerificationCodeExpiredException extends BusinessException {
    public VerificationCodeExpiredException(String message) {
        super(message);
    }
}
