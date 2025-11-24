package com.klabs.accountservice.domain.valueobject;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
//@AllArgsConstructor
public class Token {
    private final String id;
    private final String value;
    private final UUID subject;
    private final LocalDateTime expiresAt;

    public Token(String id, String value, UUID subject, LocalDateTime expiresAt) {
        if (id == null || id.isBlank())
            throw new IllegalArgumentException("Token ID cannot be null or blank");
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Cannot be null or blank");
        if (subject == null)
            throw new IllegalArgumentException("Subject cannot be null");
        if (expiresAt == null)
            throw new IllegalArgumentException("Expire time cannot be null");
        this.id = id;
        this.value = value;
        this.subject = subject;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

}
