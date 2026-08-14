package com.example.backend_pj4.application.dto.auth;

public record AuthTokenResult(String accessToken, String refreshToken, long expiresIn) {}
