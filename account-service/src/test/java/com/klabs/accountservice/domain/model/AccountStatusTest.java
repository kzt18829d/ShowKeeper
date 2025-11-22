package com.klabs.accountservice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AccountStatus Enum Tests")
class AccountStatusTest {

    // canBeDeleted() tests

    @Test
    @DisplayName("PENDING_VERIFICATION status can be deleted")
    void pendingVerificationCanBeDeleted() {
        // Arrange
        AccountStatus status = AccountStatus.PENDING_VERIFICATION;

        // Act
        boolean canBeDeleted = status.canBeDeleted();

        // Assert
        assertTrue(canBeDeleted);
    }

    @Test
    @DisplayName("ACTIVE status can be deleted")
    void activeCanBeDeleted() {
        // Arrange
        AccountStatus status = AccountStatus.ACTIVE;

        // Act
        boolean canBeDeleted = status.canBeDeleted();

        // Assert
        assertTrue(canBeDeleted);
    }

    @Test
    @DisplayName("SUSPENDED status can be deleted")
    void suspendedCanBeDeleted() {
        // Arrange
        AccountStatus status = AccountStatus.SUSPENDED;

        // Act
        boolean canBeDeleted = status.canBeDeleted();

        // Assert
        assertTrue(canBeDeleted);
    }

    @Test
    @DisplayName("DELETED status cannot be deleted")
    void deletedCannotBeDeleted() {
        // Arrange
        AccountStatus status = AccountStatus.DELETED;

        // Act
        boolean canBeDeleted = status.canBeDeleted();

        // Assert
        assertFalse(canBeDeleted);
    }

    // isActive() tests

    @Test
    @DisplayName("ACTIVE status is active")
    void activeIsActive() {
        // Arrange
        AccountStatus status = AccountStatus.ACTIVE;

        // Act
        boolean isActive = status.isActive();

        // Assert
        assertTrue(isActive);
    }

    @Test
    @DisplayName("PENDING_VERIFICATION status is not active")
    void pendingVerificationIsNotActive() {
        // Arrange
        AccountStatus status = AccountStatus.PENDING_VERIFICATION;

        // Act
        boolean isActive = status.isActive();

        // Assert
        assertFalse(isActive);
    }

    @Test
    @DisplayName("SUSPENDED status is not active")
    void suspendedIsNotActive() {
        // Arrange
        AccountStatus status = AccountStatus.SUSPENDED;

        // Act
        boolean isActive = status.isActive();

        // Assert
        assertFalse(isActive);
    }

    @Test
    @DisplayName("DELETED status is not active")
    void deletedIsNotActive() {
        // Arrange
        AccountStatus status = AccountStatus.DELETED;

        // Act
        boolean isActive = status.isActive();

        // Assert
        assertFalse(isActive);
    }

    // Enum general tests

    @Test
    @DisplayName("Should have exactly 4 enum values")
    void shouldHaveExactly4EnumValues() {
        // Act
        AccountStatus[] values = AccountStatus.values();

        // Assert
        assertEquals(4, values.length);
    }

    @Test
    @DisplayName("Should contain all expected enum values")
    void shouldContainAllExpectedEnumValues() {
        // Act
        AccountStatus[] values = AccountStatus.values();

        // Assert
        assertTrue(containsStatus(values, AccountStatus.PENDING_VERIFICATION));
        assertTrue(containsStatus(values, AccountStatus.ACTIVE));
        assertTrue(containsStatus(values, AccountStatus.SUSPENDED));
        assertTrue(containsStatus(values, AccountStatus.DELETED));
    }

    @Test
    @DisplayName("Should convert from string to enum")
    void shouldConvertFromStringToEnum() {
        // Act
        AccountStatus status = AccountStatus.valueOf("ACTIVE");

        // Assert
        assertEquals(AccountStatus.ACTIVE, status);
    }

    @Test
    @DisplayName("Should throw exception for invalid string")
    void shouldThrowExceptionForInvalidString() {
        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> AccountStatus.valueOf("INVALID_STATUS")
        );
    }

    @Test
    @DisplayName("Enum values should have correct string representation")
    void enumValuesShouldHaveCorrectStringRepresentation() {
        // Assert
        assertEquals("PENDING_VERIFICATION", AccountStatus.PENDING_VERIFICATION.name());
        assertEquals("ACTIVE", AccountStatus.ACTIVE.name());
        assertEquals("SUSPENDED", AccountStatus.SUSPENDED.name());
        assertEquals("DELETED", AccountStatus.DELETED.name());
    }

    @Test
    @DisplayName("Enum values should be comparable")
    void enumValuesShouldBeComparable() {
        // Arrange
        AccountStatus status1 = AccountStatus.PENDING_VERIFICATION;
        AccountStatus status2 = AccountStatus.ACTIVE;

        // Act
        int comparison = status1.compareTo(status2);

        // Assert
        assertTrue(comparison < 0); // PENDING_VERIFICATION comes before ACTIVE
    }

    @Test
    @DisplayName("Same enum values should be equal")
    void sameEnumValuesShouldBeEqual() {
        // Arrange
        AccountStatus status1 = AccountStatus.ACTIVE;
        AccountStatus status2 = AccountStatus.ACTIVE;

        // Act & Assert
        assertEquals(status1, status2);
        assertSame(status1, status2);
    }

    @Test
    @DisplayName("Different enum values should not be equal")
    void differentEnumValuesShouldNotBeEqual() {
        // Arrange
        AccountStatus status1 = AccountStatus.ACTIVE;
        AccountStatus status2 = AccountStatus.SUSPENDED;

        // Act & Assert
        assertNotEquals(status1, status2);
        assertNotSame(status1, status2);
    }

    // Business logic combinations

    @Test
    @DisplayName("Only ACTIVE status should be both active and deletable")
    void onlyActiveStatusShouldBeActiveAndDeletable() {
        // Arrange
        AccountStatus[] allStatuses = AccountStatus.values();

        // Act & Assert
        for (AccountStatus status : allStatuses) {
            if (status == AccountStatus.ACTIVE) {
                assertTrue(status.isActive() && status.canBeDeleted());
            } else {
                assertFalse(status.isActive() && status.canBeDeleted());
            }
        }
    }

    @Test
    @DisplayName("DELETED status should be neither active nor deletable")
    void deletedStatusShouldBeNeitherActiveNorDeletable() {
        // Arrange
        AccountStatus status = AccountStatus.DELETED;

        // Act & Assert
        assertFalse(status.isActive());
        assertFalse(status.canBeDeleted());
    }

    // Helper method
    private boolean containsStatus(AccountStatus[] values, AccountStatus status) {
        for (AccountStatus value : values) {
            if (value == status) {
                return true;
            }
        }
        return false;
    }
}