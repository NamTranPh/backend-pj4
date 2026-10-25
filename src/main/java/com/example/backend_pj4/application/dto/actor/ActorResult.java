package com.example.backend_pj4.application.dto.actor;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ActorResult(
        String id,
        String name,
        String originalName,
        String avatarUrl,
        String bio,
        LocalDate birthDate,
        String nationality,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
