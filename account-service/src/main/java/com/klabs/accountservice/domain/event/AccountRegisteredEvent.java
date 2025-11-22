package com.klabs.accountservice.domain.event;

import com.klabs.accountservice.domain.model.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class AccountRegisteredEvent implements DomainEvent {

    private final UUID aggregateID;

    private final String email;

    private final String login;

    private final LocalDateTime registrationDate;

    private final LocalDateTime occurredOn;

    public static AccountRegisteredEvent from(Account account) {
        return new AccountRegisteredEvent(account.getUuid(), account.getEmail().getValue(),
                account.getLogin().getValue(), account.getRegisterDate(), LocalDateTime.now());
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
        return "AccountRegistered";
    }
}
