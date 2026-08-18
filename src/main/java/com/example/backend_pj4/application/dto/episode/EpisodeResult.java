package com.example.backend_pj4.application.dto.episode;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.backend_pj4.common.constants.enums.VideoStatus;

public record EpisodeResult(
        String id,
        String movieId,
        Integer episodeNumber,
        String title,
        String description,
        Integer duration,
        VideoStatus status,
        String thumbnailUrl,
        String rawFileKey,
        String masterPlaylistKey,
        LocalDate airDate,
        Long viewCount,
        LocalDateTime createdAt
) {
}
