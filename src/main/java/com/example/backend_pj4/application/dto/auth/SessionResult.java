package com.example.backend_pj4.application.dto.auth;

import java.time.LocalDateTime;
public record SessionResult(
        String sessionId,
        String userAgent,
        String ipAddress,
        boolean admin,
        boolean current,
        LocalDateTime lastUsedAt,
        LocalDateTime createdAt,
        LocalDateTime expiresAt
) {}
