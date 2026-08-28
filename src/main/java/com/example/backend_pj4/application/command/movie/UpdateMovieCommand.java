package com.example.backend_pj4.application.command.movie;

import java.util.List;

import com.example.backend_pj4.common.constants.enums.MovieType;
import com.example.backend_pj4.common.constants.enums.VideoStatus;
import com.example.backend_pj4.common.constants.enums.VideoVisibility;

public record UpdateMovieCommand(
        String id,
        String title,
        String originalTitle,
        String description,
        Integer releaseYear,
        Integer duration,
        String director,
        String actors,
        String country,
        String language,
        String trailerUrl,
        MovieType movieType,
        Integer totalEpisodes,
        Boolean isFeatured,
        Boolean isPremium,
        VideoStatus status,
        VideoVisibility visibility,
        List<String> genreIds
) {
}
