package com.klabs.accountservice.domain.valueobject;


import lombok.Getter;

import java.util.Locale;
import java.util.Objects;

@Getter
public class Email {

    private final String value;

   public Email(String email) {
        validate(email);
        this.value = email.toLowerCase(Locale.ROOT).trim();
   }

   private void validate(String email) {
       if (!(email != null && !email.isBlank() && email.trim().matches("^[A-Za-z0-9]+@[A-Za-z0-9.-]+\\.[A-Za-z]+$") && email.length() <= 255))
           throw new IllegalArgumentException("Invalid email format");
   }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
