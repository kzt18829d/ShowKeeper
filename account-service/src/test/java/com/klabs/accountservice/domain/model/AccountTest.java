package com.klabs.accountservice.domain.model;

import com.klabs.accountservice.domain.service.PasswordHashingService;
import com.klabs.accountservice.domain.valueobject.Email;
import com.klabs.accountservice.domain.valueobject.Login;
import com.klabs.accountservice.domain.valueobject.Password;
import com.klabs.accountservice.shared.exception.InvalidCredentialsException;
import com.klabs.accountservice.shared.exception.OAuthProviderAlreadyBoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@DisplayName("Account Model Tests")
class AccountTest {

    private PasswordHashingService mockHashingService;
    private Login testLogin;
    private Email testEmail;
    private Password testPassword;

    @BeforeEach
    void setUp() {
        mockHashingService = Mockito.mock(PasswordHashingService.class);
        when(mockHashingService.hash(anyString())).thenReturn("$2a$10$hashedPassword");
        // Default mock for matches - can be overridden in specific tests
        when(mockHashingService.matches(anyString(), ArgumentMatchers.any(Password.class))).thenReturn(true);

        testLogin = new Login("testUser");
        testEmail = new Email("test@example.com");
        testPassword = Password.fromHash("$2a$10$hashedPassword");
    }

    // createNew() factory method tests

    @Test
    @DisplayName("Should create new Account with valid parameters")
    void shouldCreateNewAccountWithValidParameters() {
        // Act
        Account account = Account.createNew(testLogin, testEmail, testPassword);

        // Assert
        assertNotNull(account);
        assertNotNull(account.getUuid());
        assertEquals(testLogin, account.getLogin());
        assertEquals(testEmail, account.getEmail());
        assertEquals(testPassword, account.getPassword());
        assertEquals(AccountStatus.PENDING_VERIFICATION, account.getAccountStatus());
        assertNotNull(account.getRegisterDate());
        assertFalse(account.isEmailVerified());
        assertNull(account.getLastLogInDate());
    }

    @Test
    @DisplayName("Should create new Account without password (for OAuth)")
    void shouldCreateNewAccountWithoutPassword() {
        // Act
        Account account = Account.createNew(testLogin, testEmail, null);

        // Assert
        assertNotNull(account);
        assertNull(account.getPassword());
        assertFalse(account.hasPassword());
    }

    @Test
    @DisplayName("Should throw NullPointerException when login is null")
    void shouldThrowExceptionWhenLoginIsNull() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> Account.createNew(null, testEmail, testPassword)
        );
    }

    @Test
    @DisplayName("Should throw NullPointerException when email is null")
    void shouldThrowExceptionWhenEmailIsNull() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> Account.createNew(testLogin, null, testPassword)
        );
    }

    @Test
    @DisplayName("Should generate unique UUID for each new account")
    void shouldGenerateUniqueUuidForEachNewAccount() {
        // Act
        Account account1 = Account.createNew(testLogin, testEmail, testPassword);
        Account account2 = Account.createNew(testLogin, testEmail, testPassword);

        // Assert
        assertNotEquals(account1.getUuid(), account2.getUuid());
    }

    @Test
    @DisplayName("Should set register date to current time")
    void shouldSetRegisterDateToCurrentTime() {
        // Arrange
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        // Act
        Account account = Account.createNew(testLogin, testEmail, testPassword);

        // Assert
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);
        assertTrue(account.getRegisterDate().isAfter(before));
        assertTrue(account.getRegisterDate().isBefore(after));
    }

    // createNewOAuth() factory method tests

    @Test
    @DisplayName("Should create new OAuth Account with provider")
    void shouldCreateNewOAuthAccountWithProvider() {
        // Arrange
        OAuthProvider oAuthProvider = OAuthProvider.create("GOOGLE", "googleUserId123");

        // Act
        Account account = Account.createNewOAuth(testLogin, testEmail, oAuthProvider);

        // Assert
        assertNotNull(account);
        assertNull(account.getPassword());
        assertTrue(account.isEmailVerified());
        assertEquals(AccountStatus.ACTIVE, account.getAccountStatus());
    }

    @Test
    @DisplayName("Should throw NullPointerException when OAuth provider is null")
    void shouldThrowExceptionWhenOAuthProviderIsNull() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> Account.createNewOAuth(testLogin, testEmail, null)
        );
    }

    // verifyEmail() tests

    @Test
    @DisplayName("Should verify email and activate account")
    void shouldVerifyEmailAndActivateAccount() {
        // Arrange
        Account account = Account.createNew(testLogin, testEmail, testPassword);

        // Act
        account.verifyEmail();

        // Assert
        assertTrue(account.isEmailVerified());
        assertEquals(AccountStatus.ACTIVE, account.getAccountStatus());
    }

    @Test
    @DisplayName("Should only activate account if status is PENDING_VERIFICATION")
    void shouldOnlyActivateAccountIfStatusIsPendingVerification() {
        // Arrange
        Account account = Account.createNew(testLogin, testEmail, testPassword);
        account.verifyEmail(); // First verification
        account.suspend(); // Change status to SUSPENDED

        // Act
        account.verifyEmail(); // Second verification

        // Assert
        assertTrue(account.isEmailVerified());
        assertEquals(AccountStatus.SUSPENDED, account.getAccountStatus()); // Should stay SUSPENDED
    }

    // updateLogin() tests

    @Test
    @DisplayName("Should update login with new value")
    void shouldUpdateLoginWithNewValue() {
        // Arrange
        Account account = Account.createNew(testLogin, testEmail, testPassword);
        Login newLogin = new Login("newUser");

        // Act
        account.updateLogin(newLogin);

        // Assert
        assertEquals(newLogin, account.getLogin());
    }

    @Test
    @DisplayName("Should throw NullPointerException when new login is null")
    void shouldThrowExceptionWhenNewLoginIsNull() {
        // Arrange
        Account account = Account.createNew(testLogin, testEmail, testPassword);

        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> account.updateLogin(null)
        );
    }

    // updateEmail() tests

    @Test
    @DisplayName("Should update email and reset emailVerified flag")
    void shouldUpdateEmailAndResetEmailVerifiedFlag() {
        // Arrange
        Account account = Account.createNew(testLogin, testEmail, testPassword);
        account.verifyEmail(); // Verify first email
        Email newEmail = new Email("newemail@example.com");

        // Act
        account.updateEmail(newEmail);

        // Assert
        assertEquals(newEmail, account.getEmail());
        assertFalse(account.isEmailVerified()); // Should be reset
    }

    @Test
    @DisplayName("Should not reset emailVerified if email is same")
    void shouldNotResetEmailVerifiedIfEmailIsSame() {
        // Arrange
        Account account = Account.createNew(testLogin, testEmail, testPassword);
        account.verifyEmail();

        // Act
        account.updateEmail(testEmail); // Same email

        // Assert
        assertEquals(testEmail, account.getEmail());
        assertTrue(account.isEmailVerified()); // Should stay verified
    }

    @Test
    @DisplayName("Should throw NullPointerException when new email is null")
    void shouldThrowExceptionWhenNewEmailIsNull() {
        // Arrange
        Account account = Account.createNew(testLogin, testEmail, testPassword);

        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> account.updateEmail(null)
        );
    }

    // changePassword() tests

    @Test
    @DisplayName("Should change password when old password is correct")
    void shouldChangePasswordWhenOldPasswordIsCorrect() {
        // Arrange
        Account account = Account.createNew(testLogin, testEmail, testPassword);
        Password newPassword = Password.fromHash("$2a$10$newHashedPassword");
        when(mockHashingService.matches(anyString(), ArgumentMatchers.any(Password.class))).thenReturn(true);

        // Act
        account.changePassword("oldPassword123", newPassword, mockHashingService);

        // Assert
        assertEquals(newPassword, account.getPassword());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when password is not set")
    void shouldThrowExceptionWhenPasswordIsNotSet() {
        // Arrange
        Account account = Account.createNew(testLogin, testEmail, null); // No password
        Password newPassword = Password.fromHash("$2a$10$newHashedPassword");

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> account.changePassword("anyPassword", newPassword, mockHashingService)
        );
        assertEquals("Password not set", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when old password is wrong")
    void shouldThrowExceptionWhenOldPasswordIsWrong() {
        // Arrange
        Account account = Account.createNew(testLogin, testEmail, testPassword);
        Password newPassword = Password.fromHash("$2a$10$newHashedPassword");
        when(mockHashingService.matches(anyString(), ArgumentMatchers.any(Password.class))).thenReturn(false);

        // Act & Assert
        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> account.changePassword("wrongPassword", newPassword, mockHashingService)
        );
        assertEquals("Invalid old password", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw NullPointerException when parameters are null in changePassword")
    void shouldThrowExceptionWhenParametersAreNullInChangePassword() {
        // Arrange
        Account account = Account.createNew(testLogin, testEmail, testPassword);
        Password newPassword = Password.fromHash("$2a$10$newHashedPassword");

        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> account.changePassword(null, newPassword, mockHashingService));
        assertThrows(NullPointerException.class,
                () -> account.changePassword("oldPassword", null, mockHashingService));
        assertThrows(NullPointerException.class,
                () -> account.changePassword("oldPassword", newPassword, null));
    }

    // recordLogIn() tests

    @Test
    @DisplayName("Should record login date")
    void shouldRecordLoginDate() {
        // Arrange
        Account account = Account.createNew(testLogin, testEmail, testPassword);
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        // Act
        account.recordLogIn();

        // Assert
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);
        assertNotNull(account.getLastLogInDate());
        assertTrue(account.getLastLogInDate().isAfter(before));
        assertTrue(account.getLastLogInDate().isBefore(after));
    }

    @Test
    @DisplayName("Should update login date on subsequent logins")
    void shouldUpdateLoginDateOnSubsequentLogins() throws InterruptedException {
        // Arrange
        Account account = Account.createNew(testLogin, testEmail, testPassword);
        account.recordLogIn();
        LocalDateTime firstLogin = account.getLastLogInDate();
        Thread.sleep(10); // Small delay

        // Act
        account.recordLogIn();

        // Assert
        assertNotEquals(firstLogin, account.getLastLogInDate());
        assertTrue(account.getLastLogInDate().isAfter(firstLogin));
    }

    // suspend() tests

    @Test
    @DisplayName("Should suspend account when status allows deletion")
    void shouldSuspendAccountWhenStatusAllowsDeletion() {
        // Arrange
        Account account = Account.createNew(testLogin, testEmail, testPassword);
        account.verifyEmail(); // Make it ACTIVE

        // Act
        account.suspend();

        // Assert
        assertEquals(AccountStatus.SUSPENDED, account.getAccountStatus());
    }

    @Test
    @DisplayName("Should not suspend already deleted account")
    void shouldNotSuspendAlreadyDeletedAccount() {
        // Arrange
        Account account = Account.createNew(testLogin, testEmail, testPassword);
        account.verifyEmail();
        account.markAsDeleted();

        // Act
        account.suspend();

        // Assert
        assertEquals(AccountStatus.DELETED, account.getAccountStatus());
    }

    // activate() tests

    @Test
    @DisplayName("Should activate suspended account")
    void shouldActivateSuspendedAccount() {
        // Arrange
        Account account = Account.createNew(testLogin, testEmail, testPassword);
        account.verifyEmail();
        account.suspend();

        // Act
        account.activate();

        // Assert
        assertEquals(AccountStatus.ACTIVE, account.getAccountStatus());
    }

    @Test
    @DisplayName("Should not activate account if not suspended")
    void shouldNotActivateAccountIfNotSuspended() {
        // Arrange
        Account account = Account.createNew(testLogin, testEmail, testPassword);
        // Status is PENDING_VERIFICATION

        // Act
        account.activate();

        // Assert
        assertEquals(AccountStatus.PENDING_VERIFICATION, account.getAccountStatus());
    }

    // markAsDeleted() tests

    @Test
    @DisplayName("Should mark account as deleted")
    void shouldMarkAccountAsDeleted() {
        // Arrange
        Account account = Account.createNew(testLogin, testEmail, testPassword);
        account.verifyEmail(); // Make it ACTIVE

        // Act
        account.markAsDeleted();

        // Assert
        assertEquals(AccountStatus.DELETED, account.getAccountStatus());
    }

    @Test
    @DisplayName("Should not delete already deleted account")
    void shouldNotDeleteAlreadyDeletedAccount() {
        // Arrange
        Account account = Account.createNew(testLogin, testEmail, testPassword);
        account.verifyEmail();
        account.markAsDeleted();
        AccountStatus beforeSecondDelete = account.getAccountStatus();

        // Act
        account.markAsDeleted();

        // Assert
        assertEquals(beforeSecondDelete, account.getAccountStatus());
        assertEquals(AccountStatus.DELETED, account.getAccountStatus());
    }

    // hasPassword() tests

    @Test
    @DisplayName("Should return true when password is set")
    void shouldReturnTrueWhenPasswordIsSet() {
        // Arrange
        Account account = Account.createNew(testLogin, testEmail, testPassword);

        // Act
        boolean hasPassword = account.hasPassword();

        // Assert
        assertTrue(hasPassword);
    }

    @Test
    @DisplayName("Should return false when password is not set")
    void shouldReturnFalseWhenPasswordIsNotSet() {
        // Arrange
        Account account = Account.createNew(testLogin, testEmail, null);

        // Act
        boolean hasPassword = account.hasPassword();

        // Assert
        assertFalse(hasPassword);
    }

    // canLogIn() tests

    @Test
    @DisplayName("Should return true when account is active and email verified")
    void shouldReturnTrueWhenAccountIsActiveAndEmailVerified() {
        // Arrange
        Account account = Account.createNew(testLogin, testEmail, testPassword);
        account.verifyEmail();

        // Act
        boolean canLogIn = account.canLogIn();

        // Assert
        assertTrue(canLogIn);
    }

    @Test
    @DisplayName("Should return false when email is not verified")
    void shouldReturnFalseWhenEmailIsNotVerified() {
        // Arrange
        Account account = Account.createNew(testLogin, testEmail, testPassword);

        // Act
        boolean canLogIn = account.canLogIn();

        // Assert
        assertFalse(canLogIn);
    }

    @Test
    @DisplayName("Should return false when account is suspended")
    void shouldReturnFalseWhenAccountIsSuspended() {
        // Arrange
        Account account = Account.createNew(testLogin, testEmail, testPassword);
        account.verifyEmail();
        account.suspend();

        // Act
        boolean canLogIn = account.canLogIn();

        // Assert
        assertFalse(canLogIn);
    }

    @Test
    @DisplayName("Should return false when account is deleted")
    void shouldReturnFalseWhenAccountIsDeleted() {
        // Arrange
        Account account = Account.createNew(testLogin, testEmail, testPassword);
        account.verifyEmail();
        account.markAsDeleted();

        // Act
        boolean canLogIn = account.canLogIn();

        // Assert
        assertFalse(canLogIn);
    }

    // addOAuthProvider() tests

    @Test
    @DisplayName("Should add OAuth provider to account")
    void shouldAddOAuthProviderToAccount() {
        // Arrange
        Account account = Account.createNew(testLogin, testEmail, testPassword);
        OAuthProvider provider = OAuthProvider.create("GOOGLE", "googleUserId123");

        // Act
        account.addOAuthProvider(provider);

        // Assert
        assertNotNull(account.getOAuthProviders());
        assertEquals(1, account.getOAuthProviders().size());
        assertEquals("GOOGLE", account.getOAuthProviders().getFirst().getProviderName());
        assertEquals("googleUserId123", account.getOAuthProviders().getFirst().getProviderUserID());
    }

    @Test
    @DisplayName("Should throw OAuthProviderAlreadyBoundException when same provider type exists")
    void shouldThrowExceptionWhenSameProviderTypeExists() {
        // Arrange
        OAuthProvider provider1 = OAuthProvider.create("GOOGLE", "user1");
        OAuthProvider provider2 = OAuthProvider.create("GOOGLE", "user2");
        Account account = Account.createNewOAuth(testLogin, testEmail, provider1);

        // Act & Assert
        assertThrows(
                OAuthProviderAlreadyBoundException.class,
                () -> account.addOAuthProvider(provider2)
        );
    }

    @Test
    @DisplayName("Should throw NullPointerException when OAuth provider is null")
    void shouldThrowExceptionWhenOAuthProviderIsNullInAdd() {
        // Arrange
        Account account = Account.createNew(testLogin, testEmail, testPassword);

        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> account.addOAuthProvider(null)
        );
    }

    // Constructor tests

    @Test
    @DisplayName("Should create Account using constructor with all parameters")
    void shouldCreateAccountUsingConstructorWithAllParameters() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        LocalDateTime registrationDate = LocalDateTime.now();

        // Act
        Account account = new Account(uuid, testLogin, testEmail, testPassword,
                AccountStatus.ACTIVE, registrationDate, true);

        // Assert
        assertEquals(uuid, account.getUuid());
        assertEquals(testLogin, account.getLogin());
        assertEquals(testEmail, account.getEmail());
        assertEquals(testPassword, account.getPassword());
        assertEquals(AccountStatus.ACTIVE, account.getAccountStatus());
        assertEquals(registrationDate, account.getRegisterDate());
        assertTrue(account.isEmailVerified());
        assertNull(account.getLastLogInDate());
    }

    @Test
    @DisplayName("Should throw NullPointerException when required constructor parameters are null")
    void shouldThrowExceptionWhenRequiredConstructorParametersAreNull() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        LocalDateTime registrationDate = LocalDateTime.now();

        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> new Account(null, testLogin, testEmail, testPassword,
                        AccountStatus.ACTIVE, registrationDate, true));
        assertThrows(NullPointerException.class,
                () -> new Account(uuid, null, testEmail, testPassword,
                        AccountStatus.ACTIVE, registrationDate, true));
        assertThrows(NullPointerException.class,
                () -> new Account(uuid, testLogin, null, testPassword,
                        AccountStatus.ACTIVE, registrationDate, true));
        assertThrows(NullPointerException.class,
                () -> new Account(uuid, testLogin, testEmail, testPassword,
                        null, registrationDate, true));
        assertThrows(NullPointerException.class,
                () -> new Account(uuid, testLogin, testEmail, testPassword,
                        AccountStatus.ACTIVE, null, true));
    }
}
