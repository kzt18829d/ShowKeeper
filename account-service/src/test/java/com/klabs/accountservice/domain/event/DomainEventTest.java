package com.klabs.accountservice.domain.event;

import com.klabs.accountservice.domain.model.Account;
import com.klabs.accountservice.domain.model.AccountStatus;
import com.klabs.accountservice.domain.model.OAuthProvider;
import com.klabs.accountservice.domain.valueobject.Email;
import com.klabs.accountservice.domain.valueobject.Login;
import com.klabs.accountservice.domain.valueobject.Password;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Domain Event Tests")
class DomainEventTest {

    private Account testAccount;
    private UUID testUuid;

    @BeforeEach
    void setUp() {
        testUuid = UUID.randomUUID();
        Login login = new Login("testUser");
        Email email = new Email("test@example.com");
        Password password = Password.fromHash("$2a$10$hashedPassword");
        testAccount = Account.createNew(login, email, password);
    }

    // AccountRegisteredEvent tests

    @Test
    @DisplayName("Should create AccountRegisteredEvent from Account")
    void shouldCreateAccountRegisteredEventFromAccount() {
        // Act
        AccountRegisteredEvent event = AccountRegisteredEvent.from(testAccount);

        // Assert
        assertNotNull(event);
        assertEquals(testAccount.getUuid(), event.getAggregateID());
        assertEquals(testAccount.getEmail().getValue(), event.getEmail());
        assertEquals(testAccount.getLogin().getValue(), event.getLogin());
        assertEquals(testAccount.getRegisterDate(), event.getRegistrationDate());
        assertNotNull(event.getOccurredOn());
    }

    @Test
    @DisplayName("AccountRegisteredEvent should have correct event type")
    void accountRegisteredEventShouldHaveCorrectEventType() {
        // Act
        AccountRegisteredEvent event = AccountRegisteredEvent.from(testAccount);

        // Assert
        assertEquals("AccountRegistered", event.getEventType());
    }

    @Test
    @DisplayName("AccountRegisteredEvent occurredOn should be current time")
    void accountRegisteredEventOccurredOnShouldBeCurrentTime() {
        // Arrange
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        // Act
        AccountRegisteredEvent event = AccountRegisteredEvent.from(testAccount);

        // Assert
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);
        assertTrue(event.getOccurredOn().isAfter(before));
        assertTrue(event.getOccurredOn().isBefore(after));
    }

    // AccountDeletedEvent tests

    @Test
    @DisplayName("Should create AccountDeletedEvent from Account")
    void shouldCreateAccountDeletedEventFromAccount() {
        // Act
        AccountDeletedEvent event = AccountDeletedEvent.from(testAccount);

        // Assert
        assertNotNull(event);
        assertEquals(testAccount.getUuid(), event.getAggregateID());
        assertEquals(testAccount.getEmail().getValue(), event.getEmail());
        assertEquals(testAccount.getLogin().getValue(), event.getLogin());
        assertNotNull(event.getDeletedAt());
        assertNotNull(event.getOccurredOn());
    }

    @Test
    @DisplayName("AccountDeletedEvent should have correct event type")
    void accountDeletedEventShouldHaveCorrectEventType() {
        // Act
        AccountDeletedEvent event = AccountDeletedEvent.from(testAccount);

        // Assert
        assertEquals("AccountDeleted", event.getEventType());
    }

    @Test
    @DisplayName("AccountDeletedEvent deletedAt should be current time")
    void accountDeletedEventDeletedAtShouldBeCurrentTime() {
        // Arrange
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        // Act
        AccountDeletedEvent event = AccountDeletedEvent.from(testAccount);

        // Assert
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);
        assertTrue(event.getDeletedAt().isAfter(before));
        assertTrue(event.getDeletedAt().isBefore(after));
    }

    // EmailUpdatedEvent tests

    @Test
    @DisplayName("Should create EmailUpdatedEvent")
    void shouldCreateEmailUpdatedEvent() {
        // Arrange
        String oldEmail = "old@example.com";
        String newEmail = "new@example.com";

        // Act
        EmailUpdatedEvent event = EmailUpdatedEvent.create(testUuid, oldEmail, newEmail);

        // Assert
        assertNotNull(event);
        assertEquals(testUuid, event.getAggregateID());
        assertEquals(oldEmail, event.getOldEmail());
        assertEquals(newEmail, event.getNewEmail());
        assertNotNull(event.getOccurredOn());
    }

    @Test
    @DisplayName("EmailUpdatedEvent should have correct event type")
    void emailUpdatedEventShouldHaveCorrectEventType() {
        // Act
        EmailUpdatedEvent event = EmailUpdatedEvent.create(testUuid, "old@example.com", "new@example.com");

        // Assert
        assertEquals("EmailUpdated", event.getEventType());
    }

    @Test
    @DisplayName("EmailUpdatedEvent occurredOn should be current time")
    void emailUpdatedEventOccurredOnShouldBeCurrentTime() {
        // Arrange
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        // Act
        EmailUpdatedEvent event = EmailUpdatedEvent.create(testUuid, "old@example.com", "new@example.com");

        // Assert
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);
        assertTrue(event.getOccurredOn().isAfter(before));
        assertTrue(event.getOccurredOn().isBefore(after));
    }

    // LoginUpdatedEvent tests

    @Test
    @DisplayName("Should create LoginUpdatedEvent")
    void shouldCreateLoginUpdatedEvent() {
        // Arrange
        String oldLogin = "oldUser";
        String newLogin = "newUser";

        // Act
        LoginUpdatedEvent event = LoginUpdatedEvent.create(testUuid, oldLogin, newLogin);

        // Assert
        assertNotNull(event);
        assertEquals(testUuid, event.getAggregateID());
        assertEquals(oldLogin, event.getOldLogin());
        assertEquals(newLogin, event.getNewLogin());
        assertNotNull(event.getOccurredOn());
    }

    @Test
    @DisplayName("LoginUpdatedEvent should have correct event type")
    void loginUpdatedEventShouldHaveCorrectEventType() {
        // Act
        LoginUpdatedEvent event = LoginUpdatedEvent.create(testUuid, "oldUser", "newUser");

        // Assert
        assertEquals("LoginUpdated", event.getEventType());
    }

    // PasswordUpdatedEvent tests

    @Test
    @DisplayName("Should create PasswordUpdatedEvent for password change")
    void shouldCreatePasswordUpdatedEventForPasswordChange() {
        // Act
        PasswordUpdatedEvent event = PasswordUpdatedEvent.changed(testUuid);

        // Assert
        assertNotNull(event);
        assertEquals(testUuid, event.getAggregateID());
        assertFalse(event.isWasSet());
        assertNotNull(event.getOccurredOn());
    }

    @Test
    @DisplayName("Should create PasswordUpdatedEvent for password set")
    void shouldCreatePasswordUpdatedEventForPasswordSet() {
        // Act
        PasswordUpdatedEvent event = PasswordUpdatedEvent.set(testUuid);

        // Assert
        assertNotNull(event);
        assertEquals(testUuid, event.getAggregateID());
        assertTrue(event.isWasSet());
        assertNotNull(event.getOccurredOn());
    }

    @Test
    @DisplayName("PasswordUpdatedEvent should have correct event type")
    void passwordUpdatedEventShouldHaveCorrectEventType() {
        // Act
        PasswordUpdatedEvent event = PasswordUpdatedEvent.changed(testUuid);

        // Assert
        assertEquals("PasswordUpdated", event.getEventType());
    }

    @Test
    @DisplayName("PasswordUpdatedEvent wasSet should distinguish between set and changed")
    void passwordUpdatedEventWasSetShouldDistinguishBetweenSetAndChanged() {
        // Act
        PasswordUpdatedEvent changedEvent = PasswordUpdatedEvent.changed(testUuid);
        PasswordUpdatedEvent setEvent = PasswordUpdatedEvent.set(testUuid);

        // Assert
        assertFalse(changedEvent.isWasSet());
        assertTrue(setEvent.isWasSet());
    }

    // OAuthBoundEvent tests

    @Test
    @DisplayName("Should create OAuthBoundEvent from account UUID and provider")
    void shouldCreateOAuthBoundEventFromAccountUuidAndProvider() {
        // Arrange
        OAuthProvider provider = OAuthProvider.create("GOOGLE", "googleUserId123");

        // Act
        OAuthBoundEvent event = OAuthBoundEvent.from(testUuid, provider);

        // Assert
        assertNotNull(event);
        assertEquals(testUuid, event.getAggregateID());
        assertEquals("GOOGLE", event.getProviderName());
        assertEquals("googleUserId123", event.getProviderUserID());
        assertNotNull(event.getOccurredOn());
    }

    @Test
    @DisplayName("OAuthBoundEvent should have correct event type")
    void oAuthBoundEventShouldHaveCorrectEventType() {
        // Arrange
        OAuthProvider provider = OAuthProvider.create("GOOGLE", "googleUserId123");

        // Act
        OAuthBoundEvent event = OAuthBoundEvent.from(testUuid, provider);

        // Assert
        assertEquals("OAuthBound", event.getEventType());
    }

    // AccountStatusChangedEvent tests

    @Test
    @DisplayName("Should create AccountStatusChangedEvent")
    void shouldCreateAccountStatusChangedEvent() {
        // Arrange
        AccountStatus oldStatus = AccountStatus.PENDING_VERIFICATION;
        AccountStatus newStatus = AccountStatus.ACTIVE;

        // Act
        AccountStatusChangedEvent event = AccountStatusChangedEvent.create(testUuid, oldStatus, newStatus);

        // Assert
        assertNotNull(event);
        assertEquals(testUuid, event.getAggregateID());
        assertEquals(oldStatus, event.getOldStatus());
        assertEquals(newStatus, event.getNewStatus());
        assertNotNull(event.getOccurredOn());
    }

    @Test
    @DisplayName("AccountStatusChangedEvent should have correct event type")
    void accountStatusChangedEventShouldHaveCorrectEventType() {
        // Act
        AccountStatusChangedEvent event = AccountStatusChangedEvent.create(
                testUuid,
                AccountStatus.PENDING_VERIFICATION,
                AccountStatus.ACTIVE
        );

        // Assert
        assertEquals("AccountStatusChanged", event.getEventType());
    }

    @Test
    @DisplayName("AccountStatusChangedEvent should track status transitions")
    void accountStatusChangedEventShouldTrackStatusTransitions() {
        // Arrange
        AccountStatus[] statusTransitions = {
                AccountStatus.PENDING_VERIFICATION,
                AccountStatus.ACTIVE,
                AccountStatus.SUSPENDED,
                AccountStatus.DELETED
        };

        // Act & Assert
        for (int i = 0; i < statusTransitions.length - 1; i++) {
            AccountStatus oldStatus = statusTransitions[i];
            AccountStatus newStatus = statusTransitions[i + 1];

            AccountStatusChangedEvent event = AccountStatusChangedEvent.create(testUuid, oldStatus, newStatus);

            assertEquals(oldStatus, event.getOldStatus());
            assertEquals(newStatus, event.getNewStatus());
        }
    }

    // General DomainEvent interface tests

    @Test
    @DisplayName("All events should implement DomainEvent interface")
    void allEventsShouldImplementDomainEventInterface() {
        // Arrange & Act
        DomainEvent event1 = AccountRegisteredEvent.from(testAccount);
        DomainEvent event2 = AccountDeletedEvent.from(testAccount);
        DomainEvent event3 = EmailUpdatedEvent.create(testUuid, "old@test.com", "new@test.com");
        DomainEvent event4 = LoginUpdatedEvent.create(testUuid, "oldUser", "newUser");
        DomainEvent event5 = PasswordUpdatedEvent.changed(testUuid);
        DomainEvent event6 = OAuthBoundEvent.from(testUuid, OAuthProvider.create("GOOGLE", "userId"));
        DomainEvent event7 = AccountStatusChangedEvent.create(testUuid, AccountStatus.PENDING_VERIFICATION, AccountStatus.ACTIVE);

        // Assert
        assertNotNull(event1.getAggregateID());
        assertNotNull(event2.getAggregateID());
        assertNotNull(event3.getAggregateID());
        assertNotNull(event4.getAggregateID());
        assertNotNull(event5.getAggregateID());
        assertNotNull(event6.getAggregateID());
        assertNotNull(event7.getAggregateID());

        assertNotNull(event1.getEventType());
        assertNotNull(event2.getEventType());
        assertNotNull(event3.getEventType());
        assertNotNull(event4.getEventType());
        assertNotNull(event5.getEventType());
        assertNotNull(event6.getEventType());
        assertNotNull(event7.getEventType());

        assertNotNull(event1.getOccurredOn());
        assertNotNull(event2.getOccurredOn());
        assertNotNull(event3.getOccurredOn());
        assertNotNull(event4.getOccurredOn());
        assertNotNull(event5.getOccurredOn());
        assertNotNull(event6.getOccurredOn());
        assertNotNull(event7.getOccurredOn());
    }

    @Test
    @DisplayName("Events should have unique event types")
    void eventsShouldHaveUniqueEventTypes() {
        // Arrange & Act
        String type1 = AccountRegisteredEvent.from(testAccount).getEventType();
        String type2 = AccountDeletedEvent.from(testAccount).getEventType();
        String type3 = EmailUpdatedEvent.create(testUuid, "old@test.com", "new@test.com").getEventType();
        String type4 = LoginUpdatedEvent.create(testUuid, "oldUser", "newUser").getEventType();
        String type5 = PasswordUpdatedEvent.changed(testUuid).getEventType();
        String type6 = OAuthBoundEvent.from(testUuid, OAuthProvider.create("GOOGLE", "userId")).getEventType();
        String type7 = AccountStatusChangedEvent.create(testUuid, AccountStatus.PENDING_VERIFICATION, AccountStatus.ACTIVE).getEventType();

        // Assert - all event types should be unique
        assertNotEquals(type1, type2);
        assertNotEquals(type1, type3);
        assertNotEquals(type1, type4);
        assertNotEquals(type1, type5);
        assertNotEquals(type1, type6);
        assertNotEquals(type1, type7);
        assertNotEquals(type2, type3);
        assertNotEquals(type2, type4);
        assertNotEquals(type2, type5);
        assertNotEquals(type2, type6);
        assertNotEquals(type2, type7);
    }

    @Test
    @DisplayName("Events created at different times should have different occurredOn timestamps")
    void eventsCreatedAtDifferentTimesShouldHaveDifferentTimestamps() throws InterruptedException {
        // Act
        AccountRegisteredEvent event1 = AccountRegisteredEvent.from(testAccount);
        Thread.sleep(10); // Small delay
        AccountRegisteredEvent event2 = AccountRegisteredEvent.from(testAccount);

        // Assert
        assertNotEquals(event1.getOccurredOn(), event2.getOccurredOn());
        assertTrue(event2.getOccurredOn().isAfter(event1.getOccurredOn()));
    }
}
