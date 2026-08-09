// Enum định nghĩa danh sách các mã lỗi nghiệp vụ và hệ thống, thông điệp tương ứng cùng HTTP Status.
package com.example.backend_pj4.common.constants;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // ===== AUTH =====
    USERNAME_REQUIRED("Username is required.", HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED("Password is required.", HttpStatus.BAD_REQUEST),
    PASSWORD_INCORRECT("Password is incorrect.", HttpStatus.UNAUTHORIZED),
    ACCESS_TOKEN_INVALID_OR_EXPIRED("Access token is invalid or expired.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_INVALID_OR_EXPIRED("Refresh token is invalid or expired.", HttpStatus.UNAUTHORIZED),
    PHONE_NOT_FOUND("Phone is not found.", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND("User is not found.", HttpStatus.NOT_FOUND),
    OLD_PASSWORD_REQUIRED("Old password is required.", HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_REQUIRED("New password is required.", HttpStatus.BAD_REQUEST),
    OLD_PASSWORD_INCORRECT("Old password is incorrect.", HttpStatus.UNAUTHORIZED),

    // ===== SYSTEM & FRAMEWORK =====
    BAD_CREDENTIALS("Bad credentials.", HttpStatus.UNAUTHORIZED),
    VALIDATION_FAILED("Validation failed.", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND("Resource not found.", HttpStatus.NOT_FOUND),
    ACCESS_DENIED("Access denied.", HttpStatus.FORBIDDEN),
    BAD_REQUEST("Bad request.", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final HttpStatus status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
