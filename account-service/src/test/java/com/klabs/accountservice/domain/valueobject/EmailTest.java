package com.klabs.accountservice.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Email Value Object Tests")
class EmailTest {

    @Test
    @DisplayName("Should create Email with valid email address")
    void shouldCreateEmailWithValidEmailAddress() {
        // Arrange
        String validEmail = "test@example.com";

        // Act
        Email email = new Email(validEmail);

        // Assert
        assertNotNull(email);
        assertEquals("test@example.com", email.getValue());
    }

    @Test
    @DisplayName("Should convert email to lowercase")
    void shouldConvertEmailToLowercase() {
        // Arrange
        String mixedCaseEmail = "Test@Example.COM";

        // Act
        Email email = new Email(mixedCaseEmail);

        // Assert
        assertEquals("test@example.com", email.getValue());
    }

    @Test
    @DisplayName("Should trim email address")
    void shouldTrimEmailAddress() {
        // Arrange
        String emailWithSpaces = "  test@example.com  ";

        // Act
        Email email = new Email(emailWithSpaces);

        // Assert
        assertEquals("test@example.com", email.getValue());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when email is null")
    void shouldThrowExceptionWhenEmailIsNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Email(null)
        );
        assertEquals("Invalid email format", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("Should throw IllegalArgumentException when email is blank")
    void shouldThrowExceptionWhenEmailIsBlank(String blankEmail) {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Email(blankEmail)
        );
        assertEquals("Invalid email format", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid",
            "invalid@",
            "@example.com",
            "test@@example.com",
            "test@example",
            "test user@example.com",
            "test@exam ple.com",
            "test+tag@example.com"  // Special characters not allowed in current regex
    })
    @DisplayName("Should throw IllegalArgumentException for invalid email formats")
    void shouldThrowExceptionForInvalidEmailFormats(String invalidEmail) {
        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> new Email(invalidEmail)
        );
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when email exceeds 255 characters")
    void shouldThrowExceptionWhenEmailExceeds255Characters() {
        // Arrange
        String longEmail = "a".repeat(250) + "@example.com"; // 262 characters

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Email(longEmail)
        );
        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    @DisplayName("Should accept email with exactly 255 characters")
    void shouldAcceptEmailWithExactly255Characters() {
        // Arrange
        String maxLengthEmail = "a".repeat(243) + "@example.com"; // Exactly 255 characters

        // Act
        Email email = new Email(maxLengthEmail);

        // Assert
        assertNotNull(email);
        assertEquals(255, email.getValue().length());
    }

    @Test
    @DisplayName("Should accept email with numbers and dots")
    void shouldAcceptEmailWithNumbersAndDots() {
        // Arrange
        String validEmail = "user123@test.example.com";

        // Act
        Email email = new Email(validEmail);

        // Assert
        assertNotNull(email);
        assertEquals("user123@test.example.com", email.getValue());
    }

    @Test
    @DisplayName("Two emails with same value should be equal")
    void shouldBeEqualWhenValuesAreSame() {
        // Arrange
        Email email1 = new Email("test@example.com");
        Email email2 = new Email("test@example.com");

        // Act & Assert
        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
    }

    @Test
    @DisplayName("Two emails with different values should not be equal")
    void shouldNotBeEqualWhenValuesAreDifferent() {
        // Arrange
        Email email1 = new Email("test1@example.com");
        Email email2 = new Email("test2@example.com");

        // Act & Assert
        assertNotEquals(email1, email2);
    }

    @Test
    @DisplayName("Email should be equal to itself")
    void shouldBeEqualToItself() {
        // Arrange
        Email email = new Email("test@example.com");

        // Act & Assert
        assertEquals(email, email);
    }

    @Test
    @DisplayName("Email should not be equal to null")
    void shouldNotBeEqualToNull() {
        // Arrange
        Email email = new Email("test@example.com");

        // Act & Assert
        assertNotEquals(null, email);
    }

    @Test
    @DisplayName("Email should not be equal to object of different class")
    void shouldNotBeEqualToObjectOfDifferentClass() {
        // Arrange
        Email email = new Email("test@example.com");
        String string = "test@example.com";

        // Act & Assert
        assertNotEquals(email, string);
    }

    @Test
    @DisplayName("Emails with same value but different case should be equal after normalization")
    void shouldBeEqualAfterCaseNormalization() {
        // Arrange
        Email email1 = new Email("Test@Example.COM");
        Email email2 = new Email("test@example.com");

        // Act & Assert
        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
    }
}