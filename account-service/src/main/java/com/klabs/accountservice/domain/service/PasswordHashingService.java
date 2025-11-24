package com.klabs.accountservice.domain.service;

import com.klabs.accountservice.domain.valueobject.Password;


public interface PasswordHashingService {

    String hash(String plainPassword);

    boolean matches(Password plainPassword, Password hashedPassword);
}
