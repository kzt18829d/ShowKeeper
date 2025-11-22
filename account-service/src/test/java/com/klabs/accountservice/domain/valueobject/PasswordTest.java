package com.klabs.accountservice.domain.valueobject;

import com.klabs.accountservice.domain.service.PasswordHashingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@DisplayName("Password Value Object Tests")
class PasswordTest {

    private PasswordHashingService mockHashingService;

    @BeforeEach
    void setUp() {
        mockHashingService = Mockito.mock(PasswordHashingService.class);
        when(mockHashingService.hash(anyString())).thenReturn("$2a$10$hashedPasswordValue");
    }

    // fromHash() tests

    @Test
    @DisplayName("Should create Password from valid hash")
    void shouldCreatePasswordFromValidHash() {
        // Arrange
        String hash = "$2a$10$validHashedPassword";

        // Act
        Password password = Password.fromHash(hash);

        // Assert
        assertNotNull(password);
        assertEquals(hash, password.getHashedValue());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when hash is null")
    void shouldThrowExceptionWhenHashIsNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Password.fromHash(null)
        );
        assertEquals("Hashed password cannot be null or blank", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("Should throw IllegalArgumentException when hash is blank")
    void shouldThrowExceptionWhenHashIsBlank(String blankHash) {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Password.fromHash(blankHash)
        );
        assertEquals("Hashed password cannot be null or blank", exception.getMessage());
    }

    // fromPlainText() tests

    @Test
    @DisplayName("Should create Password from valid plain text password")
    void shouldCreatePasswordFromValidPlainText() {
        // Arrange
        String plainPassword = "ValidPass123";

        // Act
        Password password = Password.fromPlainText(plainPassword, mockHashingService);

        // Assert
        assertNotNull(password);
        assertEquals("$2a$10$hashedPasswordValue", password.getHashedValue());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when plain password is null")
    void shouldThrowExceptionWhenPlainPasswordIsNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Password.fromPlainText(null, mockHashingService)
        );
        assertEquals("Password cannot be null or blank", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("Should throw IllegalArgumentException when plain password is blank")
    void shouldThrowExceptionWhenPlainPasswordIsBlank(String blankPassword) {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Password.fromPlainText(blankPassword, mockHashingService)
        );
        assertEquals("Password cannot be null or blank", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "short",        // Less than 8 characters
            "abc",
            "1234567"       // Exactly 7 characters
    })
    @DisplayName("Should throw IllegalArgumentException when password is less than 8 characters")
    void shouldThrowExceptionWhenPasswordTooShort(String shortPassword) {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Password.fromPlainText(shortPassword, mockHashingService)
        );
        assertEquals("Password must be at least 8 characters long", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "lowercase123",     // No uppercase
            "alllowercase1"
    })
    @DisplayName("Should throw IllegalArgumentException when password has no uppercase letter")
    void shouldThrowExceptionWhenNoUppercaseLetter(String passwordWithoutUppercase) {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Password.fromPlainText(passwordWithoutUppercase, mockHashingService)
        );
        assertEquals("Password must contain at least one uppercase letter", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "UPPERCASE123",     // No lowercase
            "ALLUPPERCASE1"
    })
    @DisplayName("Should throw IllegalArgumentException when password has no lowercase letter")
    void shouldThrowExceptionWhenNoLowercaseLetter(String passwordWithoutLowercase) {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Password.fromPlainText(passwordWithoutLowercase, mockHashingService)
        );
        assertEquals("Password must contain at least one lowercase letter", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "NoDigitsHere",     // No digits
            "PasswordOnly"
    })
    @DisplayName("Should throw IllegalArgumentException when password has no digit")
    void shouldThrowExceptionWhenNoDigit(String passwordWithoutDigit) {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Password.fromPlainText(passwordWithoutDigit, mockHashingService)
        );
        assertEquals("Password must contain at least one digit", exception.getMessage());
    }

    @Test
    @DisplayName("Should accept password with exactly 8 characters meeting all requirements")
    void shouldAcceptMinimumValidPassword() {
        // Arrange
        String validPassword = "Pass1234"; // 8 chars, has uppercase, lowercase, and digit

        // Act
        Password password = Password.fromPlainText(validPassword, mockHashingService);

        // Assert
        assertNotNull(password);
    }

    @Test
    @DisplayName("Should accept password with special characters")
    void shouldAcceptPasswordWithSpecialCharacters() {
        // Arrange
        String validPassword = "Pass123!@#$";

        // Act
        Password password = Password.fromPlainText(validPassword, mockHashingService);

        // Assert
        assertNotNull(password);
    }

    @Test
    @DisplayName("Should accept long password meeting all requirements")
    void shouldAcceptLongPassword() {
        // Arrange
        String validPassword = "VeryLongPassword1234567890WithManyCharacters";

        // Act
        Password password = Password.fromPlainText(validPassword, mockHashingService);

        // Assert
        assertNotNull(password);
    }

    @Test
    @DisplayName("Should accept password with multiple uppercase letters")
    void shouldAcceptPasswordWithMultipleUppercase() {
        // Arrange
        String validPassword = "PASSword123";

        // Act
        Password password = Password.fromPlainText(validPassword, mockHashingService);

        // Assert
        assertNotNull(password);
    }

    @Test
    @DisplayName("Should accept password with multiple digits")
    void shouldAcceptPasswordWithMultipleDigits() {
        // Arrange
        String validPassword = "Password1234567890";

        // Act
        Password password = Password.fromPlainText(validPassword, mockHashingService);

        // Assert
        assertNotNull(password);
    }

    @Test
    @DisplayName("Should accept password with spaces if it meets requirements")
    void shouldAcceptPasswordWithSpaces() {
        // Arrange
        String validPassword = "Pass Word 123"; // Contains space but meets all requirements

        // Act
        Password password = Password.fromPlainText(validPassword, mockHashingService);

        // Assert
        assertNotNull(password);
    }

    @Test
    @DisplayName("Should call hashing service when creating from plain text")
    void shouldCallHashingServiceWhenCreatingFromPlainText() {
        // Arrange
        String plainPassword = "ValidPass123";
        PasswordHashingService spyHashingService = Mockito.spy(PasswordHashingService.class);
        when(spyHashingService.hash(plainPassword)).thenReturn("$2a$10$mockedHash");

        // Act
        Password password = Password.fromPlainText(plainPassword, spyHashingService);

        // Assert
        Mockito.verify(spyHashingService).hash(plainPassword);
        assertEquals("$2a$10$mockedHash", password.getHashedValue());
    }

    @Test
    @DisplayName("Should create different Password instances from same plain text")
    void shouldCreateDifferentInstancesFromSamePlainText() {
        // Arrange
        String plainPassword = "ValidPass123";

        // Act
        Password password1 = Password.fromPlainText(plainPassword, mockHashingService);
        Password password2 = Password.fromPlainText(plainPassword, mockHashingService);

        // Assert
        assertNotSame(password1, password2);
        assertEquals(password1.getHashedValue(), password2.getHashedValue());
    }
}