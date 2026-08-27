package com.example.backend_pj4.application.dto.history;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WatchHistoryResult(
        String id,
        String movieId,
        String movieSlug,
        String movieTitle,
        String posterUrl,
        String episodeId,
        Integer episodeNumber,
        String episodeTitle,
        Integer lastPosition,
        Integer totalDuration,
        BigDecimal progress,
        Boolean isCompleted,
        LocalDateTime watchedAt
) {
}
