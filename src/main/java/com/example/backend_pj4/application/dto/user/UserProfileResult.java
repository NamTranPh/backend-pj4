package com.example.backend_pj4.application.dto.user;

import java.time.LocalDate;
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
        LocalDateTime createdAt,
        boolean isVip,
        String currentPlanName,
        LocalDate membershipEndDate
) {
    public UserProfileResult(
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
    ) {
        this(id, email, name, phone, address, profileUrl, role, accountStatus, isBanned, emailVerified, createdAt, false, null, null);
    }
}
