package com.klabs.accountservice.shared.exception;

public class InvalidVerificationCodeException extends BusinessException {
    public InvalidVerificationCodeException(String message) {
        super(message);
    }
}
