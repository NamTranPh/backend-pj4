package com.example.backend_pj4.application.dto.auth;

public record RegisterResult(String userId, String email, boolean otpSent) {
    
}
