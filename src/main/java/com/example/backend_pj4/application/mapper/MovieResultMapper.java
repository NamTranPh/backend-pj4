package com.example.backend_pj4.application.mapper;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.application.dto.actor.MovieActorResult;
import com.example.backend_pj4.application.dto.episode.EpisodeResult;
import com.example.backend_pj4.application.dto.genre.GenreResult;
import com.example.backend_pj4.application.dto.movie.MovieDetailResult;
import com.example.backend_pj4.application.dto.movie.MovieResult;
import com.example.backend_pj4.application.service.StorageUrlResolver;
import com.example.backend_pj4.domain.model.Movie;

@Component
public class MovieResultMapper {

    private final StorageUrlResolver storageUrlResolver;
    private final ActorResultMapper actorResultMapper;

    public MovieResultMapper(StorageUrlResolver storageUrlResolver, ActorResultMapper actorResultMapper) {
        this.storageUrlResolver = storageUrlResolver;
        this.actorResultMapper = actorResultMapper;
    }

    public MovieResult toResult(Movie movie) {
        return toResult(movie, 0L, 0L);
    }

    public MovieResult toResult(Movie movie, Long ratingCount, Long commentCount) {
        List<GenreResult> genres = movie.getGenres() != null
                ? movie.getGenres().stream().map(GenreResultMapper::toResult).toList()
                : Collections.emptyList();

        List<MovieActorResult> movieActors = movie.getMovieActors() != null
                ? movie.getMovieActors().stream().map(actorResultMapper::toMovieActorResult).toList()
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
                movieActors,
                movie.getCountry(),
                movie.getLanguage(),
                movie.getTrailerUrl(),
                storageUrlResolver.resolvePublicImage(movie.getPosterUrl()),
                storageUrlResolver.resolvePublicImage(movie.getBackdropUrl()),
                movie.getMovieType(),
                movie.getTotalEpisodes(),
                movie.getStatus(),
                movie.getVisibility(),
                movie.getIsPremium(),
                movie.getIsFeatured(),
                movie.getAgeRating(),
                movie.getVideoQualities(),
                movie.getAudioTracks(),
                movie.getRating(),
                ratingCount != null ? ratingCount : 0L,
                commentCount != null ? commentCount : 0L,
                movie.getViewCount(),
                genres,
                movie.getCreatedAt(),
                movie.getUpdatedAt()
        );
    }

    public MovieDetailResult toDetailResult(Movie movie, List<EpisodeResult> episodes) {
        return toDetailResult(movie, episodes, false);
    }

    public MovieDetailResult toDetailResult(Movie movie, List<EpisodeResult> episodes, Boolean isFavorite) {
        List<GenreResult> genres = movie.getGenres() != null
                ? movie.getGenres().stream().map(GenreResultMapper::toResult).toList()
                : Collections.emptyList();

        List<MovieActorResult> movieActors = movie.getMovieActors() != null
                ? movie.getMovieActors().stream().map(actorResultMapper::toMovieActorResult).toList()
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
                movieActors,
                movie.getCountry(),
                movie.getLanguage(),
                movie.getTrailerUrl(),
                storageUrlResolver.resolvePublicImage(movie.getPosterUrl()),
                storageUrlResolver.resolvePublicImage(movie.getBackdropUrl()),
                movie.getMovieType(),
                movie.getTotalEpisodes(),
                movie.getStatus(),
                movie.getVisibility(),
                movie.getIsPremium(),
                movie.getIsFeatured(),
                movie.getAgeRating(),
                movie.getVideoQualities(),
                movie.getAudioTracks(),
                movie.getRating(),
                movie.getViewCount(),
                movie.getRawFileKey(),
                movie.getMasterPlaylistKey(),
                genres,
                episodes,
                createdByName,
                movie.getCreatedAt(),
                movie.getUpdatedAt(),
                Boolean.TRUE.equals(isFavorite)
        );
    }
}
