package com.klabs.accountservice.domain.model;

public enum AccountStatus {
    PENDING_VERIFICATION,
    ACTIVE,
    SUSPENDED,
    DELETED;

    public boolean canBeDeleted() {
        return this != DELETED;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }
}
