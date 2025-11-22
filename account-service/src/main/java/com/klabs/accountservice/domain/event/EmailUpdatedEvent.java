package com.klabs.accountservice.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class EmailUpdatedEvent implements DomainEvent{

    private final UUID aggregateID;

    private final String oldEmail;

    private final String newEmail;

    private final LocalDateTime occurredOn;

    public static EmailUpdatedEvent create(UUID accountUUID, String oldEmail, String newEmail) {
        return new EmailUpdatedEvent(accountUUID, oldEmail, newEmail, LocalDateTime.now());
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
        return "EmailUpdated";
    }
}
