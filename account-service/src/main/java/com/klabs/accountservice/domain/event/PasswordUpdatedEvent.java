package com.klabs.accountservice.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PasswordUpdatedEvent implements DomainEvent{

    private final UUID aggregateID;

    private final boolean wasSet;

    private final LocalDateTime occurredOn;

    public static PasswordUpdatedEvent changed(UUID accountUUID) {
        return new PasswordUpdatedEvent(accountUUID, false, LocalDateTime.now());
    }

    public static PasswordUpdatedEvent set(UUID accountUUID) {
        return new PasswordUpdatedEvent(accountUUID, true, LocalDateTime.now());
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
        return "PasswordUpdated";
    }
}
