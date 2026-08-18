package com.example.backend_pj4.application.command.episode;

import java.time.LocalDate;

public record UpdateEpisodeCommand(
        String id,
        String movieId,
        Integer episodeNumber,
        String title,
        String description,
        Integer duration,
        LocalDate airDate
) {
}
