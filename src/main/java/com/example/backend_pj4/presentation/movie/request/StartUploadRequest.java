package com.example.backend_pj4.presentation.movie.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record StartUploadRequest(
        @NotBlank String fileName,
        @Min(1) long fileSizeBytes
) {
}
