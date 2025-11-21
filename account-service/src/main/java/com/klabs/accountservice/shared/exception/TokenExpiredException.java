package com.klabs.accountservice.shared.exception;

public class TokenExpiredException extends BusinessException {
    public TokenExpiredException(String message) {
        super(message);
    }
}
