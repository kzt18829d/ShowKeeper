package com.klabs.accountservice.domain.model;

public enum AccountStatus {
    PENDING_VERIFICATION,
    ACTIVE,
    SUSPENDED,
    DELETED;

    boolean canBeDeleted() {
        return this != DELETED;
    }

    boolean isActive() {
        return this == ACTIVE;
    }
}
