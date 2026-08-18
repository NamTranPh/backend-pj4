package com.example.backend_pj4.application.dto.genre;

import java.time.LocalDateTime;

import com.example.backend_pj4.common.constants.enums.GenreStatus;

public record GenreResult(
        String id,
        String name,
        String slug,
        String icon,
        String description,
        GenreStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
