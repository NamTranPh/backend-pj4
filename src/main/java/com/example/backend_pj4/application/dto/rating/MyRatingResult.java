package com.example.backend_pj4.application.dto.rating;

import java.time.LocalDateTime;

public record MyRatingResult(
        String id,
        String movieId,
        String movieSlug,
        String movieTitle,
        String posterUrl,
        int score,
        String review,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
