package com.klabs.accountservice.domain.valueobject;

import com.klabs.accountservice.domain.service.PasswordHashingService;
import lombok.Getter;


@Getter
public class Password {

    private final String hashedValue;

    private Password(String hashedValue) {
        this.hashedValue = hashedValue;
    }

    public static Password fromHash(String hashedValue) {
        if (hashedValue == null || hashedValue.isBlank()) {
            throw new IllegalArgumentException("Hashed password cannot be null or blank");
        }
        return new Password(hashedValue);
    }

    /**
     * Validates plain text password against requirements:
     * - Not null and not blank
     * - Minimum length: 8 characters
     * - Contains at least 1 uppercase letter (A-Z)
     * - Contains at least 1 lowercase letter (a-z)
     * - Contains at least 1 digit (0-9)
     *
     * @param plainPassword plain text password to validate
     * @throws IllegalArgumentException if password doesn't meet requirements
     */
    private static void validatePlain(String plainPassword) {
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
        if (plainPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        if (!plainPassword.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }
        if (!plainPassword.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        }
        if (!plainPassword.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must contain at least one digit");
        }
    }

    public static Password fromPlainText(String plainPassword, PasswordHashingService passwordHashingService) {
        validatePlain(plainPassword);
        String hash = passwordHashingService.hash(plainPassword);
        return fromHash(hash);
    }

}
