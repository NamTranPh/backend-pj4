package com.example.backend_pj4.domain.valueobject;

import java.util.regex.Pattern;

import com.example.backend_pj4.domain.exception.InvalidEmailException;

public final class Email {
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private final String value;

    private Email(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidEmailException("Email cannot be empty");
        }
        if (!isValid(value)) {
            throw new InvalidEmailException("Invalid email format: " + value);
        }
        this.value = value.toLowerCase().trim();
    }

    public static Email of(String value) {
        return new Email(value);
    }

    public static boolean isValid(String value) {
        return value != null && !value.isBlank() && EMAIL_PATTERN.matcher(value).matches();
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email email)) return false;
        return value.equals(email.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
