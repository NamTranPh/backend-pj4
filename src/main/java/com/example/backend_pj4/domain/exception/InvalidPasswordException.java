package com.example.backend_pj4.domain.exception;

public class InvalidPasswordException extends RuntimeException {
    private static final String CODE = "INVALID_PASSWORD";

    public InvalidPasswordException(String message) {
        super(message);
    }

    public String getCode() {
        return CODE;
    }
}
