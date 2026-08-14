package com.example.backend_pj4.application.command.user;

public record UpdateProfileCommand(
        String userId,
        String name,
        String phone,
        String address,
        String profileUrl
) {}
