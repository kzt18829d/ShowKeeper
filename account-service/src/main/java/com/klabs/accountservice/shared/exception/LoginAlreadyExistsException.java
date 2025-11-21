package com.klabs.accountservice.shared.exception;


import com.klabs.accountservice.domain.valueobject.Login;

public class LoginAlreadyExistsException extends BusinessException {
    public LoginAlreadyExistsException(String message) {
        super(message);
    }

    public LoginAlreadyExistsException(Login login) {
        super(String.format("Login %s already exists", login.getValue()));
    }
}
