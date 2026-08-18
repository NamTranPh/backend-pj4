package com.example.backend_pj4.presentation.movie.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record RecordPartRequest(
        @NotBlank String etag,
        @Min(1) long sizeBytes
) {
}
