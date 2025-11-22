package com.klabs.accountservice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OAuthProvider Model Tests")
class OAuthProviderTest {

    // create() factory method tests

    @Test
    @DisplayName("Should create OAuthProvider with valid provider name and user ID")
    void shouldCreateOAuthProviderWithValidProviderNameAndUserId() {
        // Arrange
        String providerName = "GOOGLE";
        String providerUserId = "12345678";

        // Act
        OAuthProvider provider = OAuthProvider.create(providerName, providerUserId);

        // Assert
        assertNotNull(provider);
        assertNull(provider.getId()); // ID should be null for new entities
        assertEquals(providerName, provider.getProviderName());
        assertEquals(providerUserId, provider.getProviderUserID());
        assertNotNull(provider.getLinkedAt());
    }

    @Test
    @DisplayName("Should set linkedAt to current time when creating OAuthProvider")
    void shouldSetLinkedAtToCurrentTimeWhenCreating() {
        // Arrange
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        String providerName = "GOOGLE";
        String providerUserId = "12345678";

        // Act
        OAuthProvider provider = OAuthProvider.create(providerName, providerUserId);

        // Assert
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);
        assertTrue(provider.getLinkedAt().isAfter(before));
        assertTrue(provider.getLinkedAt().isBefore(after));
    }

    @Test
    @DisplayName("Should throw IllegalStateException when provider name is null")
    void shouldThrowExceptionWhenProviderNameIsNull() {
        // Arrange
        String providerUserId = "12345678";

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> OAuthProvider.create(null, providerUserId)
        );
        assertEquals("Invalid provider data", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when provider user ID is null")
    void shouldThrowExceptionWhenProviderUserIdIsNull() {
        // Arrange
        String providerName = "GOOGLE";

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> OAuthProvider.create(providerName, null)
        );
        assertEquals("Invalid provider data", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("Should throw IllegalStateException when provider user ID is blank")
    void shouldThrowExceptionWhenProviderUserIdIsBlank(String blankUserId) {
        // Arrange
        String providerName = "GOOGLE";

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> OAuthProvider.create(providerName, blankUserId)
        );
        assertEquals("Invalid provider data", exception.getMessage());
    }

    @Test
    @DisplayName("Should accept various provider names")
    void shouldAcceptVariousProviderNames() {
        // Arrange
        String[] providerNames = {"GOOGLE", "YANDEX", "GITHUB", "FACEBOOK"};

        // Act & Assert
        for (String providerName : providerNames) {
            OAuthProvider provider = OAuthProvider.create(providerName, "userId123");
            assertEquals(providerName, provider.getProviderName());
        }
    }

    @Test
    @DisplayName("Should accept provider user ID with various formats")
    void shouldAcceptProviderUserIdWithVariousFormats() {
        // Arrange
        String[] userIds = {
                "123456789",
                "user@email.com",
                "uuid-1234-5678",
                "mixed_Format123"
        };

        // Act & Assert
        for (String userId : userIds) {
            OAuthProvider provider = OAuthProvider.create("GOOGLE", userId);
            assertEquals(userId, provider.getProviderUserID());
        }
    }

    // isSameProvider() tests

    @Test
    @DisplayName("Should return true when provider names match (case insensitive)")
    void shouldReturnTrueWhenProviderNamesMatch() {
        // Arrange
        OAuthProvider provider = OAuthProvider.create("GOOGLE", "userId123");

        // Act & Assert
        assertTrue(provider.isSameProvider("GOOGLE"));
        assertTrue(provider.isSameProvider("google"));
        assertTrue(provider.isSameProvider("Google"));
        assertTrue(provider.isSameProvider("GoOgLe"));
    }

    @Test
    @DisplayName("Should return false when provider names do not match")
    void shouldReturnFalseWhenProviderNamesDoNotMatch() {
        // Arrange
        OAuthProvider provider = OAuthProvider.create("GOOGLE", "userId123");

        // Act & Assert
        assertFalse(provider.isSameProvider("YANDEX"));
        assertFalse(provider.isSameProvider("GITHUB"));
    }

    @Test
    @DisplayName("Should be case insensitive when comparing provider names")
    void shouldBeCaseInsensitiveWhenComparingProviderNames() {
        // Arrange
        OAuthProvider provider = OAuthProvider.create("google", "userId123");

        // Act & Assert
        assertTrue(provider.isSameProvider("GOOGLE"));
        assertTrue(provider.isSameProvider("Google"));
        assertTrue(provider.isSameProvider("google"));
    }

    // isSameUser() tests

    @Test
    @DisplayName("Should return true when provider user IDs match exactly")
    void shouldReturnTrueWhenProviderUserIdsMatchExactly() {
        // Arrange
        String userId = "userId123";
        OAuthProvider provider = OAuthProvider.create("GOOGLE", userId);

        // Act & Assert
        assertTrue(provider.isSameUser(userId));
    }

    @Test
    @DisplayName("Should return false when provider user IDs do not match")
    void shouldReturnFalseWhenProviderUserIdsDoNotMatch() {
        // Arrange
        OAuthProvider provider = OAuthProvider.create("GOOGLE", "userId123");

        // Act & Assert
        assertFalse(provider.isSameUser("userId456"));
        assertFalse(provider.isSameUser("differentUserId"));
    }

    @Test
    @DisplayName("Should be case sensitive when comparing user IDs")
    void shouldBeCaseSensitiveWhenComparingUserIds() {
        // Arrange
        OAuthProvider provider = OAuthProvider.create("GOOGLE", "UserId123");

        // Act & Assert
        assertTrue(provider.isSameUser("UserId123"));
        assertFalse(provider.isSameUser("userid123"));
        assertFalse(provider.isSameUser("USERID123"));
    }

    // Constructor tests

    @Test
    @DisplayName("Should create OAuthProvider using all-args constructor")
    void shouldCreateOAuthProviderUsingAllArgsConstructor() {
        // Arrange
        Long id = 1L;
        String providerName = "GOOGLE";
        String providerUserId = "userId123";
        LocalDateTime linkedAt = LocalDateTime.now();

        // Act
        OAuthProvider provider = new OAuthProvider(id, providerName, providerUserId, linkedAt);

        // Assert
        assertEquals(id, provider.getId());
        assertEquals(providerName, provider.getProviderName());
        assertEquals(providerUserId, provider.getProviderUserID());
        assertEquals(linkedAt, provider.getLinkedAt());
    }

    @Test
    @DisplayName("Should allow null ID for new entities")
    void shouldAllowNullIdForNewEntities() {
        // Arrange
        String providerName = "GOOGLE";
        String providerUserId = "userId123";
        LocalDateTime linkedAt = LocalDateTime.now();

        // Act
        OAuthProvider provider = new OAuthProvider(null, providerName, providerUserId, linkedAt);

        // Assert
        assertNull(provider.getId());
    }

    // Business logic tests

    @Test
    @DisplayName("Two providers with same name but different users should be same provider type")
    void twoProvidersWithSameNameButDifferentUsersShouldBeSameProviderType() {
        // Arrange
        OAuthProvider provider1 = OAuthProvider.create("GOOGLE", "user1");
        OAuthProvider provider2 = OAuthProvider.create("GOOGLE", "user2");

        // Act & Assert
        assertTrue(provider1.isSameProvider(provider2.getProviderName()));
        assertFalse(provider1.isSameUser(provider2.getProviderUserID()));
    }

    @Test
    @DisplayName("Two providers with different names but same user should be different provider types")
    void twoProvidersWithDifferentNamesButSameUserShouldBeDifferentProviderTypes() {
        // Arrange
        String userId = "commonUser123";
        OAuthProvider provider1 = OAuthProvider.create("GOOGLE", userId);
        OAuthProvider provider2 = OAuthProvider.create("YANDEX", userId);

        // Act & Assert
        assertFalse(provider1.isSameProvider(provider2.getProviderName()));
        assertTrue(provider1.isSameUser(provider2.getProviderUserID()));
    }

    @Test
    @DisplayName("Should handle provider with long user ID")
    void shouldHandleProviderWithLongUserId() {
        // Arrange
        String longUserId = "a".repeat(255);

        // Act
        OAuthProvider provider = OAuthProvider.create("GOOGLE", longUserId);

        // Assert
        assertEquals(longUserId, provider.getProviderUserID());
    }

    @Test
    @DisplayName("Should handle provider with special characters in user ID")
    void shouldHandleProviderWithSpecialCharactersInUserId() {
        // Arrange
        String specialUserId = "user!@#$%^&*()_+-={}[]|:;<>?,./";

        // Act
        OAuthProvider provider = OAuthProvider.create("GOOGLE", specialUserId);

        // Assert
        assertEquals(specialUserId, provider.getProviderUserID());
    }
}
