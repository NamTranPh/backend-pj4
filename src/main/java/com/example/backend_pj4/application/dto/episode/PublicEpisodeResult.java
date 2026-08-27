package com.example.backend_pj4.application.dto.episode;

import java.time.LocalDate;

import com.example.backend_pj4.common.constants.enums.VideoStatus;

public record PublicEpisodeResult(
        Integer episodeNumber,
        String title,
        String description,
        Integer duration,
        String thumbnailUrl,
        VideoStatus status,
        LocalDate airDate
) {
}
