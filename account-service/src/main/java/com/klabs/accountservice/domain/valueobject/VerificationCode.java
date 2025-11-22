package com.klabs.accountservice.domain.valueobject;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

@Getter
public class VerificationCode {

    private final String code;

    private final LocalDateTime expiresAt;

    private VerificationCode(String code, LocalDateTime expiresAt) {
        this.code = code;
        this.expiresAt = expiresAt;
    }

    public static VerificationCode generate() {
        return new VerificationCode(String.format("%06d", new Random().nextInt(999_999)), LocalDateTime.now().plusMinutes(10));
    }

    public static VerificationCode codeOf(String code, LocalDateTime expiresAt) {
        if (!(code != null && code.matches("//d{6}")))
            throw new IllegalArgumentException("Invalid verification code");
        return new VerificationCode(code, expiresAt);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    public boolean matches(String code) {
        Objects.requireNonNull(code);
        return this.code.equals(code);
    }

    public boolean matches(VerificationCode code) {
        return this.code.equals(code.getCode());
    }
}
