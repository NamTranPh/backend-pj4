package com.example.backend_pj4.application.command.auth;

public record ResetPasswordCommand(String email, String otpCode, String newPassword) {}
