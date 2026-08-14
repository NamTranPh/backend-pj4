package com.example.backend_pj4.presentation.auth.response;

public record RegisterResponse(String userId, String email, boolean otpSent) {}
