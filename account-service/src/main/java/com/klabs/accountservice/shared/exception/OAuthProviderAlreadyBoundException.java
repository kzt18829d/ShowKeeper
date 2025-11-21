package com.klabs.accountservice.shared.exception;

public class OAuthProviderAlreadyBoundException extends BusinessException {
    public OAuthProviderAlreadyBoundException(String message) {
        super(message);
    }
}
