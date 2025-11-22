package com.klabs.accountservice.domain.valueobject;

import lombok.Getter;

import java.util.Objects;

@Getter
public class Login {

    private final String value;

    public Login(String login) {
        validate(login);
        this.value = login.trim();
    }

    private void validate(String login) {
        if (login == null || login.isBlank())
            throw new IllegalArgumentException("Invalid login format");
        if (!login.trim().matches("^[A-Za-z0-9_]+$"))
            throw new IllegalArgumentException("Invalid login format");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Login login = (Login) o;
        return Objects.equals(value, login.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
