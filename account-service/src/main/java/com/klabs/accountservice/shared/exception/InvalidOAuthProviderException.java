package com.klabs.accountservice.shared.exception;

public class InvalidOAuthProviderException extends BusinessException {
    public InvalidOAuthProviderException(String message) {
        super(message);
    }
}
