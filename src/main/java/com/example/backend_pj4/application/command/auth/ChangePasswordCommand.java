package com.example.backend_pj4.application.command.auth;

public record ChangePasswordCommand(String userId, String oldPassword, String newPassword) {}
