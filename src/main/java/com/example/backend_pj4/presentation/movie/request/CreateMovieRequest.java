package com.example.backend_pj4.presentation.movie.request;

import java.util.List;
import java.util.Set;

import com.example.backend_pj4.common.constants.enums.AgeRating;
import com.example.backend_pj4.common.constants.enums.AudioTrackType;
import com.example.backend_pj4.common.constants.enums.MovieType;
import com.example.backend_pj4.common.constants.enums.VideoQuality;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateMovieRequest(
        @NotBlank @Size(max = 255) String title,
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
        AgeRating ageRating,
        Set<VideoQuality> videoQualities,
        Set<AudioTrackType> audioTracks,
        List<String> genreIds
) {
}
