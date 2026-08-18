package com.example.backend_pj4.infrastructure.database.mappers;

import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.infrastructure.database.entities.MovieJpaEntity;
import com.example.backend_pj4.infrastructure.database.entities.UserJpaEntity;

@Component
public class MoviePersistenceMapper {

    private final GenrePersistenceMapper GenrePersistenceMapper;
    private final EpisodePersistenceMapper EpisodePersistenceMapper;
    private final UserPersistenceMapper UserPersistenceMapper;

    public MoviePersistenceMapper(GenrePersistenceMapper GenrePersistenceMapper, @Lazy EpisodePersistenceMapper EpisodePersistenceMapper, UserPersistenceMapper UserPersistenceMapper) {
        this.GenrePersistenceMapper = GenrePersistenceMapper;
        this.EpisodePersistenceMapper = EpisodePersistenceMapper;
        this.UserPersistenceMapper = UserPersistenceMapper;
    }

    public Movie toDomain(MovieJpaEntity entity) {
        if (entity == null) return null;
        return Movie.builder()
                .id(entity.getId())
                .slug(entity.getSlug())
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
                .movieType(entity.getMovieType())
                .totalEpisodes(entity.getTotalEpisodes())
                .status(entity.getStatus())
                .visibility(entity.getVisibility())
                .isPremium(entity.getIsPremium())
                .isFeatured(entity.getIsFeatured())
                .rating(entity.getRating())
                .viewCount(entity.getViewCount())
                .rawFileKey(entity.getRawFileKey())
                .masterPlaylistKey(entity.getMasterPlaylistKey())
                .resolutions(entity.getResolutions())
                .createdBy(UserPersistenceMapper.toSimpleDomain(entity.getCreatedBy()))
                .genres(entity.getGenres() != null ? entity.getGenres().stream().map(GenrePersistenceMapper::toDomain).collect(Collectors.toList()) : Collections.emptyList())
                .episodes(entity.getEpisodes() != null ? entity.getEpisodes().stream().map(EpisodePersistenceMapper::toSimpleDomain).collect(Collectors.toList()) : Collections.emptyList())
                .deletedAt(entity.getDeletedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public MovieJpaEntity toEntity(Movie domain) {
        if (domain == null) return null;
        MovieJpaEntity entity = new MovieJpaEntity();
        entity.setId(domain.getId());
        entity.setSlug(domain.getSlug());
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
        entity.setMovieType(domain.getMovieType());
        entity.setTotalEpisodes(domain.getTotalEpisodes());
        entity.setStatus(domain.getStatus());
        entity.setVisibility(domain.getVisibility());
        entity.setIsPremium(domain.getIsPremium());
        entity.setIsFeatured(domain.getIsFeatured());
        entity.setRating(domain.getRating());
        entity.setViewCount(domain.getViewCount());
        entity.setRawFileKey(domain.getRawFileKey());
        entity.setMasterPlaylistKey(domain.getMasterPlaylistKey());
        entity.setResolutions(domain.getResolutions());
        entity.setDeletedAt(domain.getDeletedAt());
        if (domain.getCreatedBy() != null && domain.getCreatedBy().getId() != null) {
            UserJpaEntity user = new UserJpaEntity();
            user.setId(domain.getCreatedBy().getId());
            entity.setCreatedBy(user);
        }
        if (domain.getGenres() != null) {
            entity.setGenres(domain.getGenres().stream().map(GenrePersistenceMapper::toEntity).collect(Collectors.toList()));
        }
        return entity;
    }

    public Movie toSimpleDomain(MovieJpaEntity entity) {
        if (entity == null) return null;
        return Movie.builder()
                .id(entity.getId())
                .slug(entity.getSlug())
                .title(entity.getTitle())
                .posterUrl(entity.getPosterUrl())
                .movieType(entity.getMovieType())
                .status(entity.getStatus())
                .rating(entity.getRating())
                .viewCount(entity.getViewCount())
                .build();
    }
}


