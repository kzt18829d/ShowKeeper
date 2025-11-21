package com.klabs.accountservice.domain.repository;

import com.klabs.accountservice.domain.model.Account;
import com.klabs.accountservice.domain.valueobject.Email;
import com.klabs.accountservice.domain.valueobject.Login;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {

    Account save(Account account);

    Optional<Account> findByUUID(UUID uuid);

    Optional<Account> findByEmail(Email email);

    Optional<Account> findByLogin(Login login);

    boolean existsByEmail(Email email);

    boolean existsByLogin(Login login);

    void delete(Account account);

    Optional<Account> findByOAuthProvider(String providerName, String providerUserId);

    List<Account> findAccountsToDelete(LocalDateTime deleteBefore);

}
