package com.klabs.accountservice.domain.model;

import com.klabs.accountservice.domain.service.PasswordHashingService;
import com.klabs.accountservice.domain.valueobject.Email;
import com.klabs.accountservice.domain.valueobject.Login;
import com.klabs.accountservice.domain.valueobject.Password;
import com.klabs.accountservice.shared.exception.InvalidCredentialsException;
import com.klabs.accountservice.shared.exception.OAuthProviderAlreadyBoundException;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Account {

    private final UUID uuid;

    private Login login;

    private Email email;

    private Password password;

    private AccountStatus accountStatus;

    private final LocalDateTime registerDate;

    private LocalDateTime lastLogInDate;

    private boolean emailVerified;

    private List<OAuthProvider> oAuthProviders;

    private boolean hasProviderType(OAuthProvider oAuthProvider) {
        return oAuthProviders.stream().anyMatch(p -> p.isSameProvider(oAuthProvider.getProviderName()));
    }

    public Account(UUID uuid, Login login, Email email, Password password, AccountStatus status, LocalDateTime registrationDate, boolean emailVerified) {
        Objects.requireNonNull(uuid);
        Objects.requireNonNull(login);
        Objects.requireNonNull(email);
        Objects.requireNonNull(status);
        Objects.requireNonNull(registrationDate);

        this.uuid = uuid;
        this.login = login;
        this.email = email;
        this.password = password;
        this.accountStatus = status;
        this.registerDate = registrationDate;
        this.emailVerified = emailVerified;
        this.lastLogInDate = null;
    }

    public static Account createNew(Login login, Email email, Password password) {
        Objects.requireNonNull(login);
        Objects.requireNonNull(email);
        return new Account(UUID.randomUUID(), login, email, password, AccountStatus.PENDING_VERIFICATION, LocalDateTime.now(), false);
    }

    public static Account createNewOAuth(Login login, Email email, OAuthProvider oAuthProvider) {
        Objects.requireNonNull(login);
        Objects.requireNonNull(email);
        Objects.requireNonNull(oAuthProvider);

        Account account = createNew(login, email, null);
        account.verifyEmail();
        account.addOAuthProvider(oAuthProvider);

        return account;
    }

    public void verifyEmail() {
        if (!emailVerified) emailVerified = true;
        if (accountStatus == AccountStatus.PENDING_VERIFICATION) accountStatus = AccountStatus.ACTIVE;
    }

    public void updateLogin(Login login) {
        Objects.requireNonNull(login);
        if (this.login.equals(login)) this.login = login;
    }

    public void updateEmail(Email email) {
        Objects.requireNonNull(email);
        if (!this.email.equals(email)) {
            this.email = email;
            this.emailVerified = false;
        }
    }

    public void changePassword(String oldPlainPassword, Password newPassword, PasswordHashingService hashingService) {
        Objects.requireNonNull(oldPlainPassword);
        Objects.requireNonNull(newPassword);
        Objects.requireNonNull(hashingService);

        if (this.password == null)
            throw new IllegalStateException("Password not set");
        if (!hashingService.matches(oldPlainPassword, newPassword))
            throw new InvalidCredentialsException("Invalid old password");
        this.password = newPassword;
    }

    public void recordLogIn() {
        lastLogInDate = LocalDateTime.now();
    }

    public void suspend() {
        if (accountStatus.canBeDeleted()) accountStatus = AccountStatus.SUSPENDED;
    }

    public void activate() {
        if (accountStatus == AccountStatus.SUSPENDED) accountStatus = AccountStatus.ACTIVE;
    }

    public void markAsDeleted() {
        if (accountStatus.canBeDeleted()) accountStatus = AccountStatus.DELETED;
    }

    public boolean hasPassword() {
        return password != null;
    }

    public boolean canLogIn() {
        return accountStatus.isActive() && emailVerified;
    }

    public void addOAuthProvider(OAuthProvider provider) {
        Objects.requireNonNull(provider);

        if (hasProviderType(provider))
            throw new OAuthProviderAlreadyBoundException(String.format("PAuth provider '%s' is already bound to this account", provider.getProviderName()));
        oAuthProviders.add(provider);
    }


}
