package com.klabs.accountservice.domain.repository;

import com.klabs.accountservice.domain.model.DeletedAccount;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeletedAccountRepository {

    DeletedAccount save(DeletedAccount deletedAccount);

    Optional<DeletedAccount> findByOriginalUUID(UUID uuid);

    void delete(DeletedAccount deletedAccount);

    List<DeletedAccount> findAccountsToPurge(LocalDateTime purgeBefore);

    boolean existsByOriginalEmail(String email);

    boolean existsByOriginalLogin(String login);

}

