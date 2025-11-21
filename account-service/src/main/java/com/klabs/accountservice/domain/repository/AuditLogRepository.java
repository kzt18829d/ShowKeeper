package com.klabs.accountservice.domain.repository;

import com.klabs.accountservice.domain.model.AuditLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AuditLogRepository {

    AuditLog save (AuditLog auditLog);

    List<AuditLog> findByAccountUUID(UUID accountUUID);

    List<AuditLog> findByAccountAction(UUID accountUUID, String action);

    List<AuditLog> findByCreatedBetween(UUID accountUUID, LocalDateTime start, LocalDateTime end);

    void deleteByUUID(UUID accountUUID);
}
