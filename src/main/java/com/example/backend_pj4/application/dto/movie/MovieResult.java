package com.example.backend_pj4.application.dto.movie;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.backend_pj4.application.dto.genre.GenreResult;
import com.example.backend_pj4.common.constants.enums.AgeRating;
import com.example.backend_pj4.common.constants.enums.AudioTrackType;
import com.example.backend_pj4.common.constants.enums.MovieType;
import com.example.backend_pj4.common.constants.enums.VideoQuality;
import com.example.backend_pj4.common.constants.enums.VideoStatus;
import com.example.backend_pj4.common.constants.enums.VideoVisibility;

import java.util.Set;
import com.example.backend_pj4.application.dto.actor.MovieActorResult;

public record MovieResult(
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
        Long ratingCount,
        Long commentCount,
        Long viewCount,
        List<GenreResult> genres,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public MovieResult(
            String id, String slug, String title, String originalTitle, String description,
            Integer releaseYear, Integer duration, String director,
            String country, String language, String trailerUrl, String posterUrl, String backdropUrl,
            MovieType movieType, Integer totalEpisodes, VideoStatus status, VideoVisibility visibility,
            Boolean isPremium, Boolean isFeatured, BigDecimal rating, Long viewCount,
            List<GenreResult> genres, LocalDateTime createdAt, LocalDateTime updatedAt
    ) {
        this(id, slug, title, originalTitle, description, releaseYear, duration, director, List.of(),
                country, language, trailerUrl, posterUrl, backdropUrl, movieType, totalEpisodes,
                status, visibility, isPremium, isFeatured, AgeRating.T18,
                Set.of(VideoQuality.HD), Set.of(AudioTrackType.VIETSUB),
                rating, null, null, viewCount, genres, createdAt, updatedAt);
    }
}
