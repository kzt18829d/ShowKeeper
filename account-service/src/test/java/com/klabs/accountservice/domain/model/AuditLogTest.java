package com.klabs.accountservice.domain.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AuditLog Model Tests")
class AuditLogTest {

    private UUID testAccountUuid;
    private String testIpAddress;
    private String testUserAgent;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        testAccountUuid = UUID.randomUUID();
        testIpAddress = "192.168.1.1";
        testUserAgent = "Mozilla/5.0";
        objectMapper = new ObjectMapper();
    }

    // create() factory method tests

    @Test
    @DisplayName("Should create AuditLog with valid parameters")
    void shouldCreateAuditLogWithValidParameters() throws JsonProcessingException {
        // Arrange
        String action = "TEST_ACTION";
        Map<String, Object> details = new HashMap<>();
        details.put("key", "value");

        // Act
        AuditLog auditLog = AuditLog.create(testAccountUuid, action, testIpAddress, testUserAgent, details);

        // Assert
        assertNotNull(auditLog);
        assertNull(auditLog.getId()); // ID should be null for new entities
        assertEquals(testAccountUuid, auditLog.getAccountUuid());
        assertEquals(action, auditLog.getAction());
        assertEquals(testIpAddress, auditLog.getIpAddress());
        assertEquals(testUserAgent, auditLog.getUserAgent());
        assertNotNull(auditLog.getDetailsJson());
        assertNotNull(auditLog.getCreatedAt());
    }

    @Test
    @DisplayName("Should create AuditLog with null details")
    void shouldCreateAuditLogWithNullDetails() throws JsonProcessingException {
        // Arrange
        String action = "TEST_ACTION";

        // Act
        AuditLog auditLog = AuditLog.create(testAccountUuid, action, testIpAddress, testUserAgent, null);

        // Assert
        assertNotNull(auditLog);
        assertEquals("null", auditLog.getDetailsJson()); // Jackson serializes null as "null"
    }

    @Test
    @DisplayName("Should create AuditLog with null IP address and user agent")
    void shouldCreateAuditLogWithNullIpAndUserAgent() throws JsonProcessingException {
        // Arrange
        String action = "TEST_ACTION";

        // Act
        AuditLog auditLog = AuditLog.create(testAccountUuid, action, null, null, null);

        // Assert
        assertNotNull(auditLog);
        assertNull(auditLog.getIpAddress());
        assertNull(auditLog.getUserAgent());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when account UUID is null")
    void shouldThrowExceptionWhenAccountUuidIsNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> AuditLog.create(null, "ACTION", testIpAddress, testUserAgent, null)
        );
        assertEquals("Account UUID can't be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when action is null")
    void shouldThrowExceptionWhenActionIsNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> AuditLog.create(testAccountUuid, null, testIpAddress, testUserAgent, null)
        );
        assertEquals("Action can't be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when action is blank")
    void shouldThrowExceptionWhenActionIsBlank() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> AuditLog.create(testAccountUuid, "   ", testIpAddress, testUserAgent, null)
        );
        assertEquals("Action can't be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("Should set createdAt to current time")
    void shouldSetCreatedAtToCurrentTime() throws JsonProcessingException {
        // Arrange
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        // Act
        AuditLog auditLog = AuditLog.create(testAccountUuid, "ACTION", testIpAddress, testUserAgent, null);

        // Assert
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);
        assertTrue(auditLog.getCreatedAt().isAfter(before));
        assertTrue(auditLog.getCreatedAt().isBefore(after));
    }

    @Test
    @DisplayName("Should serialize details to JSON correctly")
    void shouldSerializeDetailsToJsonCorrectly() throws JsonProcessingException {
        // Arrange
        Map<String, Object> details = new HashMap<>();
        details.put("field1", "value1");
        details.put("field2", 123);
        details.put("field3", true);

        // Act
        AuditLog auditLog = AuditLog.create(testAccountUuid, "ACTION", testIpAddress, testUserAgent, details);

        // Assert
        String json = auditLog.getDetailsJson();
        assertNotNull(json);
        Map<String, Object> parsedDetails = objectMapper.readValue(json, Map.class);
        assertEquals("value1", parsedDetails.get("field1"));
        assertEquals(123, parsedDetails.get("field2"));
        assertEquals(true, parsedDetails.get("field3"));
    }

    // login() convenience method tests

    @Test
    @DisplayName("Should create login AuditLog with LOGIN action")
    void shouldCreateLoginAuditLogWithLoginAction() throws JsonProcessingException {
        // Act
        AuditLog auditLog = AuditLog.login(testAccountUuid, testIpAddress, testUserAgent);

        // Assert
        assertNotNull(auditLog);
        assertEquals("LOGIN", auditLog.getAction());
        assertEquals(testAccountUuid, auditLog.getAccountUuid());
        assertEquals(testIpAddress, auditLog.getIpAddress());
        assertEquals(testUserAgent, auditLog.getUserAgent());
    }

    // passwordChanged() convenience method tests

    @Test
    @DisplayName("Should create password changed AuditLog with PASSWORD_CHANGED action")
    void shouldCreatePasswordChangedAuditLogWithPasswordChangedAction() throws JsonProcessingException {
        // Act
        AuditLog auditLog = AuditLog.passwordChanged(testAccountUuid, testIpAddress, testUserAgent);

        // Assert
        assertNotNull(auditLog);
        assertEquals("PASSWORD_CHANGED", auditLog.getAction());
        assertEquals(testAccountUuid, auditLog.getAccountUuid());
        assertEquals(testIpAddress, auditLog.getIpAddress());
        assertEquals(testUserAgent, auditLog.getUserAgent());
    }

    // emailUpdated() convenience method tests

    @Test
    @DisplayName("Should create email updated AuditLog with old and new email in details")
    void shouldCreateEmailUpdatedAuditLogWithOldAndNewEmail() throws JsonProcessingException {
        // Arrange
        String oldEmail = "old@example.com";
        String newEmail = "new@example.com";

        // Act
        AuditLog auditLog = AuditLog.emailUpdated(testAccountUuid, oldEmail, newEmail, testIpAddress, testUserAgent);

        // Assert
        assertNotNull(auditLog);
        assertEquals("EMAIL_UPDATED", auditLog.getAction());

        String json = auditLog.getDetailsJson();
        Map<String, Object> details = objectMapper.readValue(json, Map.class);
        assertEquals(oldEmail, details.get("oldEmail"));
        assertEquals(newEmail, details.get("newEmail"));
    }

    // loginUpdated() convenience method tests

    @Test
    @DisplayName("Should create login updated AuditLog with old and new login in details")
    void shouldCreateLoginUpdatedAuditLogWithOldAndNewLogin() throws JsonProcessingException {
        // Arrange
        String oldLogin = "oldUser";
        String newLogin = "newUser";

        // Act
        AuditLog auditLog = AuditLog.loginUpdated(testAccountUuid, oldLogin, newLogin, testIpAddress, testUserAgent);

        // Assert
        assertNotNull(auditLog);
        assertEquals("LOGIN_UPDATED", auditLog.getAction());

        String json = auditLog.getDetailsJson();
        Map<String, Object> details = objectMapper.readValue(json, Map.class);
        assertEquals(oldLogin, details.get("oldLogin"));
        assertEquals(newLogin, details.get("newLogin"));
    }

    // Constructor tests

    @Test
    @DisplayName("Should create AuditLog using all-args constructor")
    void shouldCreateAuditLogUsingAllArgsConstructor() {
        // Arrange
        Long id = 1L;
        String action = "TEST_ACTION";
        String detailsJson = "{\"key\":\"value\"}";
        LocalDateTime createdAt = LocalDateTime.now();

        // Act
        AuditLog auditLog = new AuditLog(id, testAccountUuid, action, testIpAddress, testUserAgent, detailsJson, createdAt);

        // Assert
        assertEquals(id, auditLog.getId());
        assertEquals(testAccountUuid, auditLog.getAccountUuid());
        assertEquals(action, auditLog.getAction());
        assertEquals(testIpAddress, auditLog.getIpAddress());
        assertEquals(testUserAgent, auditLog.getUserAgent());
        assertEquals(detailsJson, auditLog.getDetailsJson());
        assertEquals(createdAt, auditLog.getCreatedAt());
    }

    @Test
    @DisplayName("Should create AuditLog using no-args constructor")
    void shouldCreateAuditLogUsingNoArgsConstructor() {
        // Act
        AuditLog auditLog = new AuditLog();

        // Assert
        assertNotNull(auditLog);
        assertNull(auditLog.getId());
        assertNull(auditLog.getAccountUuid());
    }

    // Business logic tests

    @Test
    @DisplayName("Should handle empty details map")
    void shouldHandleEmptyDetailsMap() throws JsonProcessingException {
        // Arrange
        Map<String, Object> emptyDetails = new HashMap<>();

        // Act
        AuditLog auditLog = AuditLog.create(testAccountUuid, "ACTION", testIpAddress, testUserAgent, emptyDetails);

        // Assert
        assertEquals("{}", auditLog.getDetailsJson());
    }

    @Test
    @DisplayName("Should handle complex nested details")
    void shouldHandleComplexNestedDetails() throws JsonProcessingException {
        // Arrange
        Map<String, Object> nestedDetails = new HashMap<>();
        Map<String, Object> innerMap = new HashMap<>();
        innerMap.put("innerKey", "innerValue");
        nestedDetails.put("outerKey", innerMap);
        nestedDetails.put("arrayKey", new String[]{"item1", "item2"});

        // Act
        AuditLog auditLog = AuditLog.create(testAccountUuid, "ACTION", testIpAddress, testUserAgent, nestedDetails);

        // Assert
        String json = auditLog.getDetailsJson();
        assertNotNull(json);
        assertTrue(json.contains("outerKey"));
        assertTrue(json.contains("innerKey"));
    }

    @Test
    @DisplayName("Should handle special characters in action")
    void shouldHandleSpecialCharactersInAction() throws JsonProcessingException {
        // Arrange
        String actionWithSpecialChars = "ACTION_WITH_SPECIAL-CHARS.2024";

        // Act
        AuditLog auditLog = AuditLog.create(testAccountUuid, actionWithSpecialChars, testIpAddress, testUserAgent, null);

        // Assert
        assertEquals(actionWithSpecialChars, auditLog.getAction());
    }

    @Test
    @DisplayName("Should handle long IP address (IPv6)")
    void shouldHandleLongIpAddress() throws JsonProcessingException {
        // Arrange
        String ipv6 = "2001:0db8:85a3:0000:0000:8a2e:0370:7334";

        // Act
        AuditLog auditLog = AuditLog.create(testAccountUuid, "ACTION", ipv6, testUserAgent, null);

        // Assert
        assertEquals(ipv6, auditLog.getIpAddress());
    }

    @Test
    @DisplayName("Should handle long user agent string")
    void shouldHandleLongUserAgentString() throws JsonProcessingException {
        // Arrange
        String longUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";

        // Act
        AuditLog auditLog = AuditLog.create(testAccountUuid, "ACTION", testIpAddress, longUserAgent, null);

        // Assert
        assertEquals(longUserAgent, auditLog.getUserAgent());
    }

    @Test
    @DisplayName("Multiple audit logs should have different created timestamps")
    void multipleAuditLogsShouldHaveDifferentTimestamps() throws JsonProcessingException, InterruptedException {
        // Act
        AuditLog log1 = AuditLog.create(testAccountUuid, "ACTION1", testIpAddress, testUserAgent, null);
        Thread.sleep(10); // Small delay
        AuditLog log2 = AuditLog.create(testAccountUuid, "ACTION2", testIpAddress, testUserAgent, null);

        // Assert
        assertNotEquals(log1.getCreatedAt(), log2.getCreatedAt());
        assertTrue(log2.getCreatedAt().isAfter(log1.getCreatedAt()));
    }
}
