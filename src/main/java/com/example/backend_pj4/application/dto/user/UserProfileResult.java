package com.example.backend_pj4.application.dto.user;

import java.time.LocalDateTime;

public record UserProfileResult(
        String id,
        String email,
        String name,
        String phone,
        String address,
        String profileUrl,
        String role,
        String accountStatus,
        boolean isBanned,
        boolean emailVerified,
        LocalDateTime createdAt
) {}
