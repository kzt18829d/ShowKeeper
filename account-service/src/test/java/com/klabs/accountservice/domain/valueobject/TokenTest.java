package com.klabs.accountservice.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Token Value Object Tests")
class TokenTest {

    @Test
    @DisplayName("Should create Token with valid value and expiration time")
    void shouldCreateTokenWithValidValueAndExpirationTime() {
        // Arrange
        String tokenValue = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);

        // Act
        Token token = new Token(tokenValue, expiresAt);

        // Assert
        assertNotNull(token);
        assertEquals(tokenValue, token.getValue());
        assertEquals(expiresAt, token.getExpiresAt());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when token value is null")
    void shouldThrowExceptionWhenTokenValueIsNull() {
        // Arrange
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Token(null, expiresAt)
        );
        assertEquals("Cannot be null or blank", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("Should throw IllegalArgumentException when token value is blank")
    void shouldThrowExceptionWhenTokenValueIsBlank(String blankValue) {
        // Arrange
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Token(blankValue, expiresAt)
        );
        assertEquals("Cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when expiresAt is null")
    void shouldThrowExceptionWhenExpiresAtIsNull() {
        // Arrange
        String tokenValue = "validTokenValue";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Token(tokenValue, null)
        );
        assertEquals("Expire time cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should return false when token is not expired")
    void shouldReturnFalseWhenTokenIsNotExpired() {
        // Arrange
        String tokenValue = "validTokenValue";
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);
        Token token = new Token(tokenValue, expiresAt);

        // Act
        boolean isExpired = token.isExpired();

        // Assert
        assertFalse(isExpired);
    }

    @Test
    @DisplayName("Should return true when token is expired")
    void shouldReturnTrueWhenTokenIsExpired() {
        // Arrange
        String tokenValue = "expiredTokenValue";
        LocalDateTime expiresAt = LocalDateTime.now().minusHours(1);
        Token token = new Token(tokenValue, expiresAt);

        // Act
        boolean isExpired = token.isExpired();

        // Assert
        assertTrue(isExpired);
    }

    @Test
    @DisplayName("Should return true when token expires exactly at current time")
    void shouldReturnTrueWhenTokenExpiresExactlyNow() throws InterruptedException {
        // Arrange
        String tokenValue = "tokenExpiringNow";
        LocalDateTime expiresAt = LocalDateTime.now().minusNanos(1000);
        Token token = new Token(tokenValue, expiresAt);

        // Small delay to ensure time has passed
        Thread.sleep(1);

        // Act
        boolean isExpired = token.isExpired();

        // Assert
        assertTrue(isExpired);
    }

    @Test
    @DisplayName("Should accept token with long value")
    void shouldAcceptTokenWithLongValue() {
        // Arrange
        String longTokenValue = "a".repeat(1000);
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(1);

        // Act
        Token token = new Token(longTokenValue, expiresAt);

        // Assert
        assertNotNull(token);
        assertEquals(longTokenValue, token.getValue());
    }

    @Test
    @DisplayName("Should accept token expiring far in the future")
    void shouldAcceptTokenExpiringFarInTheFuture() {
        // Arrange
        String tokenValue = "longLivedToken";
        LocalDateTime expiresAt = LocalDateTime.now().plusYears(10);

        // Act
        Token token = new Token(tokenValue, expiresAt);

        // Assert
        assertNotNull(token);
        assertFalse(token.isExpired());
    }

    @Test
    @DisplayName("Should accept token that expired long ago")
    void shouldAcceptTokenThatExpiredLongAgo() {
        // Arrange
        String tokenValue = "veryOldToken";
        LocalDateTime expiresAt = LocalDateTime.now().minusYears(5);

        // Act
        Token token = new Token(tokenValue, expiresAt);

        // Assert
        assertNotNull(token);
        assertTrue(token.isExpired());
    }

    @Test
    @DisplayName("Should create token with special characters in value")
    void shouldCreateTokenWithSpecialCharacters() {
        // Arrange
        String tokenValue = "token!@#$%^&*()_+-={}[]|:;<>?,./";
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);

        // Act
        Token token = new Token(tokenValue, expiresAt);

        // Assert
        assertNotNull(token);
        assertEquals(tokenValue, token.getValue());
    }

    @Test
    @DisplayName("Should preserve exact token value without modification")
    void shouldPreserveExactTokenValue() {
        // Arrange
        String tokenValue = "  PreserveSpaces  ";
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);

        // Act
        Token token = new Token(tokenValue, expiresAt);

        // Assert
        assertEquals(tokenValue, token.getValue());
    }

    @Test
    @DisplayName("Should handle expiration time with nanosecond precision")
    void shouldHandleExpirationTimeWithNanosecondPrecision() {
        // Arrange
        String tokenValue = "preciseToken";
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1).plusNanos(123456789);

        // Act
        Token token = new Token(tokenValue, expiresAt);

        // Assert
        assertEquals(expiresAt, token.getExpiresAt());
    }

    @Test
    @DisplayName("Two tokens should be independent instances")
    void twoTokensShouldBeIndependentInstances() {
        // Arrange
        String tokenValue1 = "token1";
        String tokenValue2 = "token2";
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);

        // Act
        Token token1 = new Token(tokenValue1, expiresAt);
        Token token2 = new Token(tokenValue2, expiresAt);

        // Assert
        assertNotSame(token1, token2);
        assertNotEquals(token1.getValue(), token2.getValue());
    }
}