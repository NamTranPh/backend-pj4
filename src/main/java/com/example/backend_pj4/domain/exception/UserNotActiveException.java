package com.example.backend_pj4.domain.exception;

public class UserNotActiveException extends RuntimeException {
    private static final String CODE = "USER_NOT_ACTIVE";

    public UserNotActiveException(String message) {
        super(message);
    }

    public String getCode() {
        return CODE;
    }
}
