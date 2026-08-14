package com.example.backend_pj4.application.command.auth;

public record VerifyRegistrationCommand(String email, String otpCode) {}
