package com.klabs.accountservice.domain.service;

import com.klabs.accountservice.domain.model.Account;
import com.klabs.accountservice.domain.valueobject.Email;
import com.klabs.accountservice.domain.valueobject.Login;
import com.klabs.accountservice.domain.valueobject.Password;
import com.klabs.accountservice.shared.exception.*;

import java.util.UUID;

public interface AccountValidationService {

    void validateRegistration(Email email, Login login) throws EmailAlreadyExistsException, LoginAlreadyExistsException;

    void validateLoginUpdate(UUID accountUUID, Login newLogin) throws LoginAlreadyExistsException;

    void validateEmailUpdate(UUID accountUUID, Email newEmail) throws EmailAlreadyExistsException;

    void validateCanLogIn(Account account) throws AccountSuspendedException, EmailNotVerifiedException, AccountAlreadyDeletedException;

    void validatePasswordLogIn(Account account, Password password, PasswordHashingService hashingService) throws PasswordNotSetException, InvalidCredentialsException;

}
