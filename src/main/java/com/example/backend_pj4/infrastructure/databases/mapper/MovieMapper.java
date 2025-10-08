package com.example.backend_pj4.infrastructure.databases.mapper;

import com.example.backend_pj4.domain.entities.Movie;
import com.example.backend_pj4.infrastructure.databases.entities.MovieEntity;

import java.util.stream.Collectors;

public class MovieMapper {

    public static Movie toDomain(MovieEntity entity) {
        if (entity == null) return null;

        return Movie.builder()
                .movieId(entity.getMovieId())
                .title(entity.getTitle())
                .originalTitle(entity.getOriginalTitle())
                .description(entity.getDescription())
                .releaseYear(entity.getReleaseYear())
                .duration(entity.getDuration())
                .director(entity.getDirector())
                .actors(entity.getActors())
                .country(entity.getCountry())
                .language(entity.getLanguage())
                .trailerUrl(entity.getTrailerUrl())
                .posterUrl(entity.getPosterUrl())
                .backdropUrl(entity.getBackdropUrl())
                .rating(entity.getRating())
                .viewCount(entity.getViewCount())
                .movieType(entity.getMovieType() != null ? entity.getMovieType().name() : null)
                .totalEpisodes(entity.getTotalEpisodes())
                .status(entity.getStatus() != null ? entity.getStatus().name() : null)
                .isPremium(entity.getIsPremium())
                .isFeatured(entity.getIsFeatured())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .genres(entity.getGenres() != null ? entity.getGenres()
                        .stream()
                        .map(GenreMapper::toDomain)
                        .collect(Collectors.toList()) : null)
                .episodes(entity.getEpisodes() != null ? entity.getEpisodes()
                        .stream()
                        .map(EpisodeMapper::toDomain)
                        .collect(Collectors.toList()) : null)
                .build();
    }

    public static MovieEntity toEntity(Movie domain) {
        if (domain == null) return null;

        MovieEntity entity = new MovieEntity();
        entity.setMovieId(domain.getMovieId());
        entity.setTitle(domain.getTitle());
        entity.setOriginalTitle(domain.getOriginalTitle());
        entity.setDescription(domain.getDescription());
        entity.setReleaseYear(domain.getReleaseYear());
        entity.setDuration(domain.getDuration());
        entity.setDirector(domain.getDirector());
        entity.setActors(domain.getActors());
        entity.setCountry(domain.getCountry());
        entity.setLanguage(domain.getLanguage());
        entity.setTrailerUrl(domain.getTrailerUrl());
        entity.setPosterUrl(domain.getPosterUrl());
        entity.setBackdropUrl(domain.getBackdropUrl());
        entity.setRating(domain.getRating());
        entity.setViewCount(domain.getViewCount());
        // convert string -> enum
        if (domain.getMovieType() != null)
            entity.setMovieType(Enum.valueOf(com.example.backend_pj4.domain.enums.MovieType.class, domain.getMovieType()));
        if (domain.getStatus() != null)
            entity.setStatus(Enum.valueOf(com.example.backend_pj4.domain.enums.MovieStatus.class, domain.getStatus()));

        entity.setTotalEpisodes(domain.getTotalEpisodes());
        entity.setIsPremium(domain.getIsPremium());
        entity.setIsFeatured(domain.getIsFeatured());
        entity.setIsActive(domain.getIsActive());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        if (domain.getGenres() != null) {
            entity.setGenres(domain.getGenres()
                    .stream()
                    .map(GenreMapper::toEntity)
                    .collect(Collectors.toList()));
        }

        if (domain.getEpisodes() != null) {
            entity.setEpisodes(domain.getEpisodes()
                    .stream()
                    .map(EpisodeMapper::toEntity)
                    .collect(Collectors.toList()));
        }

        return entity;
    }
}
