package com.example.backend_pj4.domain.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class EmailVerification {
    private String id;
    private String userId;
    private String token;
    private LocalDateTime expiresAt;
    private LocalDateTime usedAt;
    private LocalDateTime createdAt;
}
