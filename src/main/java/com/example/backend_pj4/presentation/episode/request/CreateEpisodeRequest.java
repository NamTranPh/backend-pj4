package com.example.backend_pj4.presentation.episode.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateEpisodeRequest(
        @NotNull
        @Min(1)
        Integer episodeNumber,

        @Size(max = 255)
        String title,

        @Size(max = 2000)
        String description,

        @Min(1)
        Integer duration,

        LocalDate airDate
) {
}
