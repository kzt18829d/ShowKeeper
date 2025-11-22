package com.klabs.accountservice.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class LoginUpdatedEvent implements DomainEvent{

    private final UUID aggregateID;

    private final String oldLogin;

    private final String newLogin;

    private final LocalDateTime occurredOn;

    public static LoginUpdatedEvent create(UUID accountUUID, String oldLogin, String newLogin) {
        return new LoginUpdatedEvent(accountUUID, oldLogin, newLogin, LocalDateTime.now());
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
        return "LoginUpdated";
    }
}
