package com.klabs.accountservice.domain.valueobject;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
//@AllArgsConstructor
public class Token {
    private final String value;
    private final LocalDateTime expiresAt;

    public Token(String value, LocalDateTime expiresAt) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Cannot be null or blank");
        if (expiresAt == null)
            throw new IllegalArgumentException("Expire time cannot be null");
        this.value = value;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

}
