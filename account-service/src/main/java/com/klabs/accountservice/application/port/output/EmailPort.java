package com.klabs.accountservice.application.port.output;

public interface EmailPort {

    void sendVerificationCode(String email, String code);

    void sendPasswordResetLink(String email, String resetToken);

    void sendEmailChangeNotification(String oldEmail, String newEmail);

}
