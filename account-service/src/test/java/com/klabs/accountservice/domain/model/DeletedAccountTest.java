package com.klabs.accountservice.domain.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klabs.accountservice.domain.valueobject.Email;
import com.klabs.accountservice.domain.valueobject.Login;
import com.klabs.accountservice.domain.valueobject.Password;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DeletedAccount Model Tests")
class DeletedAccountTest {

    private Account testAccount;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        Login login = new Login("testUser");
        Email email = new Email("test@example.com");
        Password password = Password.fromHash("$2a$10$hashedPassword");
        testAccount = Account.createNew(login, email, password);
        testAccount.verifyEmail();

        objectMapper = new ObjectMapper();
    }

    // fromAccount() factory method tests

    @Test
    @DisplayName("Should create DeletedAccount from Account")
    void shouldCreateDeletedAccountFromAccount() throws JsonProcessingException {
        // Act
        DeletedAccount deletedAccount = DeletedAccount.fromAccount(testAccount);

        // Assert
        assertNotNull(deletedAccount);
        assertNull(deletedAccount.getId()); // ID should be null for new entities
        assertEquals(testAccount.getUuid(), deletedAccount.getOriginalUuid());
        assertEquals(testAccount.getLogin().getValue(), deletedAccount.getOriginalLogin());
        assertEquals(testAccount.getEmail().getValue(), deletedAccount.getOriginalEmail());
        assertNotNull(deletedAccount.getAccountDataJson());
        assertNotNull(deletedAccount.getDeletedAt());
        assertNotNull(deletedAccount.getPurgeAt());
    }

    @Test
    @DisplayName("Should set deletedAt to current time")
    void shouldSetDeletedAtToCurrentTime() throws JsonProcessingException {
        // Arrange
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        // Act
        DeletedAccount deletedAccount = DeletedAccount.fromAccount(testAccount);

        // Assert
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);
        assertTrue(deletedAccount.getDeletedAt().isAfter(before));
        assertTrue(deletedAccount.getDeletedAt().isBefore(after));
    }

    @Test
    @DisplayName("Should set purgeAt to 60 days after deletion")
    void shouldSetPurgeAtTo60DaysAfterDeletion() throws JsonProcessingException {
        // Arrange
        LocalDateTime expectedPurgeAt = LocalDateTime.now().plusDays(60);

        // Act
        DeletedAccount deletedAccount = DeletedAccount.fromAccount(testAccount);

        // Assert
        LocalDateTime purgeAt = deletedAccount.getPurgeAt();
        // Allow 1 second tolerance
        assertTrue(purgeAt.isAfter(expectedPurgeAt.minusSeconds(1)));
        assertTrue(purgeAt.isBefore(expectedPurgeAt.plusSeconds(1)));
    }

    @Test
    @DisplayName("Should serialize account data to JSON")
    void shouldSerializeAccountDataToJson() throws JsonProcessingException {
        // Act
        DeletedAccount deletedAccount = DeletedAccount.fromAccount(testAccount);

        // Assert
        String json = deletedAccount.getAccountDataJson();
        assertNotNull(json);
        assertFalse(json.isEmpty());

        // Verify JSON can be parsed
        Map<String, Object> parsedData = objectMapper.readValue(json, Map.class);
        assertNotNull(parsedData);
        assertEquals(testAccount.getUuid().toString(), parsedData.get("uuid"));
        assertEquals(testAccount.getLogin().getValue(), parsedData.get("login"));
        assertEquals(testAccount.getEmail().getValue(), parsedData.get("email"));
    }

    @Test
    @DisplayName("Should include password hash in JSON when password exists")
    void shouldIncludePasswordHashInJsonWhenPasswordExists() throws JsonProcessingException {
        // Act
        DeletedAccount deletedAccount = DeletedAccount.fromAccount(testAccount);

        // Assert
        String json = deletedAccount.getAccountDataJson();
        Map<String, Object> parsedData = objectMapper.readValue(json, Map.class);

        assertTrue((Boolean) parsedData.get("hasPassword"));
        assertNotNull(parsedData.get("passwordHash"));
        assertEquals(testAccount.getPassword().getHashedValue(), parsedData.get("passwordHash"));
    }

    @Test
    @DisplayName("Should handle account without password")
    void shouldHandleAccountWithoutPassword() throws JsonProcessingException {
        // Arrange
        Account accountWithoutPassword = Account.createNew(
                new Login("oauthUser"),
                new Email("oauth@example.com"),
                null
        );

        // Act
        DeletedAccount deletedAccount = DeletedAccount.fromAccount(accountWithoutPassword);

        // Assert
        String json = deletedAccount.getAccountDataJson();
        Map<String, Object> parsedData = objectMapper.readValue(json, Map.class);

        assertFalse((Boolean) parsedData.get("hasPassword"));
        assertNull(parsedData.get("passwordHash"));
    }

    @Test
    @DisplayName("Should include account status in JSON")
    void shouldIncludeAccountStatusInJson() throws JsonProcessingException {
        // Act
        DeletedAccount deletedAccount = DeletedAccount.fromAccount(testAccount);

        // Assert
        String json = deletedAccount.getAccountDataJson();
        Map<String, Object> parsedData = objectMapper.readValue(json, Map.class);

        assertEquals(testAccount.getAccountStatus().name(), parsedData.get("accountStatus"));
    }

    @Test
    @DisplayName("Should include register date in JSON")
    void shouldIncludeRegisterDateInJson() throws JsonProcessingException {
        // Act
        DeletedAccount deletedAccount = DeletedAccount.fromAccount(testAccount);

        // Assert
        String json = deletedAccount.getAccountDataJson();
        Map<String, Object> parsedData = objectMapper.readValue(json, Map.class);

        assertNotNull(parsedData.get("registerDate"));
        assertEquals(testAccount.getRegisterDate().toString(), parsedData.get("registerDate"));
    }

    @Test
    @DisplayName("Should include last login date when it exists")
    void shouldIncludeLastLoginDateWhenItExists() throws JsonProcessingException {
        // Arrange
        testAccount.recordLogIn();

        // Act
        DeletedAccount deletedAccount = DeletedAccount.fromAccount(testAccount);

        // Assert
        String json = deletedAccount.getAccountDataJson();
        Map<String, Object> parsedData = objectMapper.readValue(json, Map.class);

        assertNotNull(parsedData.get("lastLogInDate"));
        assertEquals(testAccount.getLastLogInDate().toString(), parsedData.get("lastLogInDate"));
    }

    @Test
    @DisplayName("Should include email verified status in JSON")
    void shouldIncludeEmailVerifiedStatusInJson() throws JsonProcessingException {
        // Act
        DeletedAccount deletedAccount = DeletedAccount.fromAccount(testAccount);

        // Assert
        String json = deletedAccount.getAccountDataJson();
        Map<String, Object> parsedData = objectMapper.readValue(json, Map.class);

        assertEquals(testAccount.isEmailVerified(), parsedData.get("emailVerified"));
    }

    @Test
    @DisplayName("Should include OAuth providers when they exist")
    void shouldIncludeOAuthProvidersWhenTheyExist() throws JsonProcessingException {
        // Arrange
        OAuthProvider provider = OAuthProvider.create("GOOGLE", "googleUserId123");
        Account oauthAccount = Account.createNewOAuth(
                new Login("oauthUser"),
                new Email("oauth@example.com"),
                provider
        );

        // Act
        DeletedAccount deletedAccount = DeletedAccount.fromAccount(oauthAccount);

        // Assert
        String json = deletedAccount.getAccountDataJson();
        assertNotNull(json);
        assertTrue(json.contains("oauthProviders"));
    }

    // canBeRestored() tests

    @Test
    @DisplayName("Should return true when account can be restored (within 60 days)")
    void shouldReturnTrueWhenAccountCanBeRestored() throws JsonProcessingException {
        // Act
        DeletedAccount deletedAccount = DeletedAccount.fromAccount(testAccount);

        // Assert
        assertTrue(deletedAccount.canBeRestored());
    }

    @Test
    @DisplayName("Should return false when purge date has passed")
    void shouldReturnFalseWhenPurgeDateHasPassed() {
        // Arrange
        LocalDateTime pastDate = LocalDateTime.now().minusDays(70);
        LocalDateTime pastPurgeDate = pastDate.plusDays(60);
        DeletedAccount deletedAccount = new DeletedAccount(
                1L,
                UUID.randomUUID(),
                "oldUser",
                "old@example.com",
                "{}",
                pastDate,
                pastPurgeDate
        );

        // Act
        boolean canBeRestored = deletedAccount.canBeRestored();

        // Assert
        assertFalse(canBeRestored);
    }

    // shouldBePurged() tests

    @Test
    @DisplayName("Should return false when purge date has not arrived")
    void shouldReturnFalseWhenPurgeDateHasNotArrived() throws JsonProcessingException {
        // Act
        DeletedAccount deletedAccount = DeletedAccount.fromAccount(testAccount);

        // Assert
        assertFalse(deletedAccount.shouldBePurged());
    }

    @Test
    @DisplayName("Should return true when purge date has passed")
    void shouldReturnTrueWhenPurgeDateHasPassed() {
        // Arrange
        LocalDateTime pastDate = LocalDateTime.now().minusDays(70);
        LocalDateTime pastPurgeDate = pastDate.plusDays(60);
        DeletedAccount deletedAccount = new DeletedAccount(
                1L,
                UUID.randomUUID(),
                "oldUser",
                "old@example.com",
                "{}",
                pastDate,
                pastPurgeDate
        );

        // Act
        boolean shouldBePurged = deletedAccount.shouldBePurged();

        // Assert
        assertTrue(shouldBePurged);
    }

    @Test
    @DisplayName("Should return true when purge date equals current time")
    void shouldReturnTrueWhenPurgeDateEqualsCurrentTime() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        DeletedAccount deletedAccount = new DeletedAccount(
                1L,
                UUID.randomUUID(),
                "user",
                "user@example.com",
                "{}",
                now.minusDays(60),
                now
        );

        // Act
        boolean shouldBePurged = deletedAccount.shouldBePurged();

        // Assert
        assertTrue(shouldBePurged);
    }

    // Constructor tests

    @Test
    @DisplayName("Should create DeletedAccount using all-args constructor")
    void shouldCreateDeletedAccountUsingAllArgsConstructor() {
        // Arrange
        Long id = 1L;
        UUID uuid = UUID.randomUUID();
        String login = "deletedUser";
        String email = "deleted@example.com";
        String json = "{\"data\":\"value\"}";
        LocalDateTime deletedAt = LocalDateTime.now();
        LocalDateTime purgeAt = LocalDateTime.now().plusDays(60);

        // Act
        DeletedAccount deletedAccount = new DeletedAccount(id, uuid, login, email, json, deletedAt, purgeAt);

        // Assert
        assertEquals(id, deletedAccount.getId());
        assertEquals(uuid, deletedAccount.getOriginalUuid());
        assertEquals(login, deletedAccount.getOriginalLogin());
        assertEquals(email, deletedAccount.getOriginalEmail());
        assertEquals(json, deletedAccount.getAccountDataJson());
        assertEquals(deletedAt, deletedAccount.getDeletedAt());
        assertEquals(purgeAt, deletedAccount.getPurgeAt());
    }

    @Test
    @DisplayName("Should create DeletedAccount using no-args constructor")
    void shouldCreateDeletedAccountUsingNoArgsConstructor() {
        // Act
        DeletedAccount deletedAccount = new DeletedAccount();

        // Assert
        assertNotNull(deletedAccount);
        assertNull(deletedAccount.getId());
        assertNull(deletedAccount.getOriginalUuid());
    }

    // Business logic tests

    @Test
    @DisplayName("canBeRestored and shouldBePurged should be complementary before purge date")
    void canBeRestoredAndShouldBePurgedShouldBeComplementary() throws JsonProcessingException {
        // Arrange
        DeletedAccount deletedAccount = DeletedAccount.fromAccount(testAccount);

        // Act & Assert
        assertTrue(deletedAccount.canBeRestored());
        assertFalse(deletedAccount.shouldBePurged());
    }

    @Test
    @DisplayName("canBeRestored and shouldBePurged should be complementary after purge date")
    void canBeRestoredAndShouldBePurgedShouldBeComplementaryAfterPurge() {
        // Arrange
        LocalDateTime pastDate = LocalDateTime.now().minusDays(70);
        LocalDateTime pastPurgeDate = pastDate.plusDays(60);
        DeletedAccount deletedAccount = new DeletedAccount(
                1L,
                UUID.randomUUID(),
                "oldUser",
                "old@example.com",
                "{}",
                pastDate,
                pastPurgeDate
        );

        // Act & Assert
        assertFalse(deletedAccount.canBeRestored());
        assertTrue(deletedAccount.shouldBePurged());
    }
}
