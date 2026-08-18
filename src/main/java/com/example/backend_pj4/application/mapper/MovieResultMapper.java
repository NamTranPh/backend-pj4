package com.example.backend_pj4.application.mapper;

import java.util.Collections;
import java.util.List;

import com.example.backend_pj4.application.dto.episode.EpisodeResult;
import com.example.backend_pj4.application.dto.genre.GenreResult;
import com.example.backend_pj4.application.dto.movie.MovieDetailResult;
import com.example.backend_pj4.application.dto.movie.MovieResult;
import com.example.backend_pj4.domain.model.Movie;

public final class MovieResultMapper {

    private MovieResultMapper() {
    }

    public static MovieResult toResult(Movie movie) {
        List<GenreResult> genres = movie.getGenres() != null
                ? movie.getGenres().stream().map(GenreResultMapper::toResult).toList()
                : Collections.emptyList();

        return new MovieResult(
                movie.getId(),
                movie.getSlug(),
                movie.getTitle(),
                movie.getOriginalTitle(),
                movie.getDescription(),
                movie.getReleaseYear(),
                movie.getDuration(),
                movie.getDirector(),
                movie.getActors(),
                movie.getCountry(),
                movie.getLanguage(),
                movie.getTrailerUrl(),
                movie.getPosterUrl(),
                movie.getBackdropUrl(),
                movie.getMovieType(),
                movie.getTotalEpisodes(),
                movie.getStatus(),
                movie.getVisibility(),
                movie.getIsPremium(),
                movie.getIsFeatured(),
                movie.getRating(),
                movie.getViewCount(),
                genres,
                movie.getCreatedAt(),
                movie.getUpdatedAt()
        );
    }

    public static MovieDetailResult toDetailResult(Movie movie, List<EpisodeResult> episodes) {
        List<GenreResult> genres = movie.getGenres() != null
                ? movie.getGenres().stream().map(GenreResultMapper::toResult).toList()
                : Collections.emptyList();

        String createdByName = movie.getCreatedBy() != null ? movie.getCreatedBy().getName() : null;

        return new MovieDetailResult(
                movie.getId(),
                movie.getSlug(),
                movie.getTitle(),
                movie.getOriginalTitle(),
                movie.getDescription(),
                movie.getReleaseYear(),
                movie.getDuration(),
                movie.getDirector(),
                movie.getActors(),
                movie.getCountry(),
                movie.getLanguage(),
                movie.getTrailerUrl(),
                movie.getPosterUrl(),
                movie.getBackdropUrl(),
                movie.getMovieType(),
                movie.getTotalEpisodes(),
                movie.getStatus(),
                movie.getVisibility(),
                movie.getIsPremium(),
                movie.getIsFeatured(),
                movie.getRating(),
                movie.getViewCount(),
                movie.getRawFileKey(),
                movie.getMasterPlaylistKey(),
                genres,
                episodes,
                createdByName,
                movie.getCreatedAt(),
                movie.getUpdatedAt()
        );
    }
}
