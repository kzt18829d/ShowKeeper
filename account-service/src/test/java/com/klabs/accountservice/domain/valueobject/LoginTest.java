package com.klabs.accountservice.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Login Value Object Tests")
class LoginTest {

    @Test
    @DisplayName("Should create Login with valid login string")
    void shouldCreateLoginWithValidLoginString() {
        // Arrange
        String validLogin = "testUser123";

        // Act
        Login login = new Login(validLogin);

        // Assert
        assertNotNull(login);
        assertEquals("testUser123", login.getValue());
    }

    @Test
    @DisplayName("Should trim login value")
    void shouldTrimLoginValue() {
        // Arrange
        String loginWithSpaces = "  testUser  ";

        // Act
        Login login = new Login(loginWithSpaces);

        // Assert
        assertEquals("testUser", login.getValue());
    }

    @Test
    @DisplayName("Should accept login with letters, numbers and underscores")
    void shouldAcceptLoginWithLettersNumbersAndUnderscores() {
        // Arrange
        String validLogin = "Test_User_123";

        // Act
        Login login = new Login(validLogin);

        // Assert
        assertEquals("Test_User_123", login.getValue());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when login is null")
    void shouldThrowExceptionWhenLoginIsNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Login(null)
        );
        assertEquals("Invalid login format", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("Should throw IllegalArgumentException when login is blank")
    void shouldThrowExceptionWhenLoginIsBlank(String blankLogin) {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Login(blankLogin)
        );
        assertEquals("Invalid login format", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "test user",        // Contains space
            "test-user",        // Contains hyphen
            "test.user",        // Contains dot
            "test@user",        // Contains @
            "test+user",        // Contains +
            "testПользователь", // Contains Cyrillic
            "test!user"         // Contains special character
    })
    @DisplayName("Should throw IllegalArgumentException for invalid login formats")
    void shouldThrowExceptionForInvalidLoginFormats(String invalidLogin) {
        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> new Login(invalidLogin)
        );
    }

    @Test
    @DisplayName("Should accept login with only letters")
    void shouldAcceptLoginWithOnlyLetters() {
        // Arrange
        String validLogin = "TestUser";

        // Act
        Login login = new Login(validLogin);

        // Assert
        assertEquals("TestUser", login.getValue());
    }

    @Test
    @DisplayName("Should accept login with only numbers")
    void shouldAcceptLoginWithOnlyNumbers() {
        // Arrange
        String validLogin = "123456";

        // Act
        Login login = new Login(validLogin);

        // Assert
        assertEquals("123456", login.getValue());
    }

    @Test
    @DisplayName("Should accept login with only underscores")
    void shouldAcceptLoginWithOnlyUnderscores() {
        // Arrange
        String validLogin = "____";

        // Act
        Login login = new Login(validLogin);

        // Assert
        assertEquals("____", login.getValue());
    }

    @Test
    @DisplayName("Should accept login starting with number")
    void shouldAcceptLoginStartingWithNumber() {
        // Arrange
        String validLogin = "123test";

        // Act
        Login login = new Login(validLogin);

        // Assert
        assertEquals("123test", login.getValue());
    }

    @Test
    @DisplayName("Should accept login starting with underscore")
    void shouldAcceptLoginStartingWithUnderscore() {
        // Arrange
        String validLogin = "_testUser";

        // Act
        Login login = new Login(validLogin);

        // Assert
        assertEquals("_testUser", login.getValue());
    }

    @Test
    @DisplayName("Two logins with same value should be equal")
    void shouldBeEqualWhenValuesAreSame() {
        // Arrange
        Login login1 = new Login("testUser");
        Login login2 = new Login("testUser");

        // Act & Assert
        assertEquals(login1, login2);
        assertEquals(login1.hashCode(), login2.hashCode());
    }

    @Test
    @DisplayName("Two logins with different values should not be equal")
    void shouldNotBeEqualWhenValuesAreDifferent() {
        // Arrange
        Login login1 = new Login("testUser1");
        Login login2 = new Login("testUser2");

        // Act & Assert
        assertNotEquals(login1, login2);
    }

    @Test
    @DisplayName("Login should be equal to itself")
    void shouldBeEqualToItself() {
        // Arrange
        Login login = new Login("testUser");

        // Act & Assert
        assertEquals(login, login);
    }

    @Test
    @DisplayName("Login should not be equal to null")
    void shouldNotBeEqualToNull() {
        // Arrange
        Login login = new Login("testUser");

        // Act & Assert
        assertNotEquals(null, login);
    }

    @Test
    @DisplayName("Login should not be equal to object of different class")
    void shouldNotBeEqualToObjectOfDifferentClass() {
        // Arrange
        Login login = new Login("testUser");
        String string = "testUser";

        // Act & Assert
        assertNotEquals(login, string);
    }

    @Test
    @DisplayName("Logins are case-sensitive")
    void shouldBeCaseSensitive() {
        // Arrange
        Login login1 = new Login("TestUser");
        Login login2 = new Login("testuser");

        // Act & Assert
        assertNotEquals(login1, login2);
    }

    @Test
    @DisplayName("Should preserve original case")
    void shouldPreserveOriginalCase() {
        // Arrange
        String originalLogin = "TestUser123";

        // Act
        Login login = new Login(originalLogin);

        // Assert
        assertEquals("TestUser123", login.getValue());
    }
}