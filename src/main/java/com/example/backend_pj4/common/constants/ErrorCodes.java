package com.example.backend_pj4.common.constants;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCodes {

    // ===== AUTH =====
    USERNAME_REQUIRED(1001, "Username is required.", HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED(1002, "Password is required.", HttpStatus.BAD_REQUEST),
    PASSWORD_INCORRECT(1003, "Password is incorrect.", HttpStatus.UNAUTHORIZED),
    ACCESS_TOKEN_INVALID_OR_EXPIRED(1104, "Access token is invalid or expired.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_INVALID_OR_EXPIRED(1105, "Refresh token is invalid or expired.", HttpStatus.UNAUTHORIZED),
    PHONE_NOT_FOUND(1106, "Phone is not found.", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND(1107, "User is not found.", HttpStatus.NOT_FOUND),
    OLD_PASSWORD_REQUIRED(1108, "Old password is required.", HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_REQUIRED(1109, "New password is required.", HttpStatus.BAD_REQUEST),
    OLD_PASSWORD_INCORRECT(1110, "Old password is incorrect.", HttpStatus.UNAUTHORIZED);

    private final int code;
    private final String message;
    private final HttpStatus status;

    ErrorCodes(int code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
