package com.example.backend_pj4.presentation.history.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateWatchProgressRequest(
        @NotBlank String movieId,
        String episodeId,
        @NotNull @Min(0) Integer positionSeconds,
        @NotNull @Min(1) Integer durationSeconds
) {
}
