package com.example.backend_pj4.presentation.genre.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateGenreRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        @Size(max = 255)
        String icon,

        @Size(max = 500)
        String description
) {
}
