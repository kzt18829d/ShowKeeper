package com.klabs.accountservice.domain.event;

import com.klabs.accountservice.domain.model.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class AccountStatusChangedEvent implements DomainEvent {

    private final UUID aggregateID;

    private final AccountStatus oldStatus;

    private final AccountStatus newStatus;

    private final LocalDateTime occurredOn;

    public static AccountStatusChangedEvent create(UUID accountUUID, AccountStatus oldStatus, AccountStatus newStatus) {
        return new AccountStatusChangedEvent(accountUUID, oldStatus, newStatus, LocalDateTime.now());
    }

    @Override
    public UUID getAggregateID() {
        return aggregateID;
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    @Override
    public String getEventType() {
        return "AccountStatusChanged";
    }
}
