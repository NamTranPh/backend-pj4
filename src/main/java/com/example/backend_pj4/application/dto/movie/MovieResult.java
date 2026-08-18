package com.example.backend_pj4.application.dto.movie;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.backend_pj4.application.dto.genre.GenreResult;
import com.example.backend_pj4.common.constants.enums.MovieType;
import com.example.backend_pj4.common.constants.enums.VideoStatus;
import com.example.backend_pj4.common.constants.enums.VideoVisibility;

public record MovieResult(
        String id,
        String slug,
        String title,
        String originalTitle,
        String description,
        Integer releaseYear,
        Integer duration,
        String director,
        String actors,
        String country,
        String language,
        String trailerUrl,
        String posterUrl,
        String backdropUrl,
        MovieType movieType,
        Integer totalEpisodes,
        VideoStatus status,
        VideoVisibility visibility,
        Boolean isPremium,
        Boolean isFeatured,
        BigDecimal rating,
        Long viewCount,
        List<GenreResult> genres,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
