package com.klabs.accountservice.domain.event;

import com.klabs.accountservice.domain.model.OAuthProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class OAuthBoundEvent implements DomainEvent{

    private final UUID aggregateID;

    private final String providerName;

    private final String providerUserID;

    private final LocalDateTime occurredOn;

    public static OAuthBoundEvent from(UUID accountUUID, OAuthProvider provider) {
        return new OAuthBoundEvent(accountUUID, provider.getProviderName(), provider.getProviderUserID(), LocalDateTime.now());
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
        return "OAuthBound";
    }
}
