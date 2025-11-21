package com.klabs.accountservice.shared.exception;

import com.klabs.accountservice.domain.valueobject.Email;

public class EmailAlreadyExistsException extends BusinessException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }

    public EmailAlreadyExistsException(Email email) {
        super(String.format("Email %s already exists", email.getValue()));
    }
}
