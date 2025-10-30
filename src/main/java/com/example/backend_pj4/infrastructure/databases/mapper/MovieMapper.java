package com.example.backend_pj4.infrastructure.databases.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.entities.Movie;
import com.example.backend_pj4.domain.enums.MovieStatus;
import com.example.backend_pj4.domain.enums.MovieType;
import com.example.backend_pj4.infrastructure.databases.entities.MovieEntity;

@Component
public class MovieMapper {

    private final GenreMapper genreMapper;
    private final EpisodeMapper episodeMapper;

    public MovieMapper(
            GenreMapper genreMapper,
            @Lazy EpisodeMapper episodeMapper
    ) {
        this.genreMapper = genreMapper;
        this.episodeMapper = episodeMapper;
    }

    // ==============================
    // Entity → Domain
    // ==============================
    public Movie toDomain(MovieEntity entity) {
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
                        .map(genreMapper::toDomain)
                        .collect(Collectors.toList()) : List.of())
                .episodes(entity.getEpisodes() != null ? entity.getEpisodes()
                        .stream()
                        .map(episodeMapper::toSimpleDomain) // dùng bản đơn giản tránh loop
                        .collect(Collectors.toList()) : List.of())
                .build();
    }

    // ==============================
    // Domain → Entity
    // ==============================
    public MovieEntity toEntity(Movie domain) {
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

        if (domain.getMovieType() != null) {
            entity.setMovieType(MovieType.valueOf(domain.getMovieType()));
        }
        if (domain.getStatus() != null) {
            entity.setStatus(MovieStatus.valueOf(domain.getStatus()));
        }

        entity.setTotalEpisodes(domain.getTotalEpisodes());
        entity.setIsPremium(domain.getIsPremium());
        entity.setIsFeatured(domain.getIsFeatured());
        entity.setIsActive(domain.getIsActive());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        if (domain.getGenres() != null) {
            entity.setGenres(domain.getGenres()
                    .stream()
                    .map(genreMapper::toEntity)
                    .collect(Collectors.toList()));
        }

        if (domain.getEpisodes() != null) {
            entity.setEpisodes(domain.getEpisodes()
                    .stream()
                    .map(episodeMapper::toEntity)
                    .collect(Collectors.toList()));
        }

        return entity;
    }

    // ==============================
    // Simple mapping (tránh vòng lặp)
    // ==============================
    public Movie toSimpleDomain(MovieEntity entity) {
        if (entity == null) return null;
        return Movie.builder()
                .movieId(entity.getMovieId())
                .title(entity.getTitle())
                .posterUrl(entity.getPosterUrl())
                .rating(entity.getRating())
                .build();
    }
}
