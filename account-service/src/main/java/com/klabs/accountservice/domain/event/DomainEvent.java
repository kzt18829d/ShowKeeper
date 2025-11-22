package com.klabs.accountservice.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public interface DomainEvent {

    UUID getAggregateID();

    LocalDateTime getOccurredOn();

    String getEventType();

}
