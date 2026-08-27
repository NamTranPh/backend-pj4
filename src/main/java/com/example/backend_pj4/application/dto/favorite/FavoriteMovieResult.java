package com.example.backend_pj4.application.dto.favorite;

import java.time.LocalDateTime;

public record FavoriteMovieResult(
        String movieId,
        String movieSlug,
        String movieTitle,
        String posterUrl,
        Boolean isPremium,
        LocalDateTime addedAt
) {
}
