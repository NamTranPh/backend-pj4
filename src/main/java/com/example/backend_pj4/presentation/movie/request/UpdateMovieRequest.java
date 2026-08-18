package com.example.backend_pj4.presentation.movie.request;

import java.util.List;

import com.example.backend_pj4.common.constants.enums.MovieType;

import jakarta.validation.constraints.Size;

public record UpdateMovieRequest(
        @Size(max = 255) String title,
        @Size(max = 255) String originalTitle,
        @Size(max = 5000) String description,
        Integer releaseYear,
        Integer duration,
        @Size(max = 255) String director,
        @Size(max = 2000) String actors,
        @Size(max = 100) String country,
        @Size(max = 50) String language,
        @Size(max = 500) String trailerUrl,
        MovieType movieType,
        Integer totalEpisodes,
        Boolean isFeatured,
        List<String> genreIds
) {
}
