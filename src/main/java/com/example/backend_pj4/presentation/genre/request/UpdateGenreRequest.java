package com.example.backend_pj4.presentation.genre.request;

import com.example.backend_pj4.common.constants.enums.GenreStatus;

import jakarta.validation.constraints.Size;

public record UpdateGenreRequest(
        @Size(max = 100)
        String name,

        @Size(max = 255)
        String icon,

        @Size(max = 500)
        String description,

        GenreStatus status
) {
}
