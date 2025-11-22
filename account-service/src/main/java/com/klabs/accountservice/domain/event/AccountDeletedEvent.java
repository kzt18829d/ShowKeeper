package com.klabs.accountservice.domain.event;

import com.klabs.accountservice.domain.model.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class AccountDeletedEvent implements  DomainEvent{

    private final UUID aggregateID;

    private final String email;

    private final String login;

    private final LocalDateTime deletedAt;

    private final LocalDateTime occurredOn;

    public static AccountDeletedEvent from(Account account) {
        return new AccountDeletedEvent(account.getUuid(), account.getEmail().getValue(),
                account.getLogin().getValue(), LocalDateTime.now(), LocalDateTime.now());
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
        return "AccountDeleted";
    }
}
