package com.example.backend_pj4.application.dto.movie;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.backend_pj4.application.dto.episode.EpisodeResult;
import com.example.backend_pj4.application.dto.genre.GenreResult;
import com.example.backend_pj4.common.constants.enums.AgeRating;
import com.example.backend_pj4.common.constants.enums.AudioTrackType;
import com.example.backend_pj4.common.constants.enums.MovieType;
import com.example.backend_pj4.common.constants.enums.VideoQuality;
import com.example.backend_pj4.common.constants.enums.VideoStatus;
import com.example.backend_pj4.common.constants.enums.VideoVisibility;

import java.util.Set;
import com.example.backend_pj4.application.dto.actor.MovieActorResult;

public record MovieDetailResult(
        String id,
        String slug,
        String title,
        String originalTitle,
        String description,
        Integer releaseYear,
        Integer duration,
        String director,
        List<MovieActorResult> movieActors,
        String country,
        String language,
        String trailerUrl,
        String posterUrl,
        String backdropUrl,
        MovieType movieType,
        Integer totalEpisodes,
        VideoStatus status,
        VideoVisibility visibility,
        Boolean isPremium,
        Boolean isFeatured,
        AgeRating ageRating,
        Set<VideoQuality> videoQualities,
        Set<AudioTrackType> audioTracks,
        BigDecimal rating,
        Long viewCount,
        String rawFileKey,
        String masterPlaylistKey,
        List<GenreResult> genres,
        List<EpisodeResult> episodes,
        String createdByName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Boolean isFavorite
) {
    public MovieDetailResult(
            String id,
            String slug,
            String title,
            String originalTitle,
            String description,
            Integer releaseYear,
            Integer duration,
            String director,
            String country,
            String language,
            String trailerUrl,
            String posterUrl,
            String backdropUrl,
            MovieType movieType,
            Integer totalEpisodes,
            VideoStatus status,
            VideoVisibility visibility,
            Boolean isPremium,
            Boolean isFeatured,
            AgeRating ageRating,
            Set<VideoQuality> videoQualities,
            Set<AudioTrackType> audioTracks,
            BigDecimal rating,
            Long viewCount,
            String rawFileKey,
            String masterPlaylistKey,
            List<GenreResult> genres,
            List<EpisodeResult> episodes,
            String createdByName,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this(id, slug, title, originalTitle, description, releaseYear, duration, director, List.of(), country, language,
                trailerUrl, posterUrl, backdropUrl, movieType, totalEpisodes, status, visibility, isPremium, isFeatured,
                ageRating, videoQualities, audioTracks,
                rating, viewCount, rawFileKey, masterPlaylistKey, genres, episodes, createdByName, createdAt, updatedAt, false);
    }
}
