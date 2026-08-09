package com.example.backend_pj4.domain.exception;

public class InvalidEmailException extends RuntimeException {
    private static final String CODE = "INVALID_EMAIL";

    public InvalidEmailException(String message) {
        super(message);
    }

    public String getCode() {
        return CODE;
    }
}
