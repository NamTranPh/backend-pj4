package com.example.backend_pj4.infrastructure.database.mappers;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.model.Episode;
import com.example.backend_pj4.infrastructure.database.entities.EpisodeJpaEntity;
import com.example.backend_pj4.infrastructure.database.entities.MovieJpaEntity;

@Component
public class EpisodePersistenceMapper {

    public Episode toDomain(EpisodeJpaEntity entity) {
        if (entity == null) return null;
        return Episode.builder()
                .id(entity.getId())
                .movieId(entity.getMovie() != null ? entity.getMovie().getId() : null)
                .episodeNumber(entity.getEpisodeNumber())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .duration(entity.getDuration())
                .status(entity.getStatus())
                .rawFileKey(entity.getRawFileKey())
                .masterPlaylistKey(entity.getMasterPlaylistKey())
                .resolutions(entity.getResolutions())
                .thumbnailUrl(entity.getThumbnailUrl())
                .airDate(entity.getAirDate())
                .isPremium(entity.getIsPremium())
                .viewCount(entity.getViewCount())
                .deletedAt(entity.getDeletedAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public EpisodeJpaEntity toEntity(Episode domain) {
        if (domain == null) return null;
        EpisodeJpaEntity entity = new EpisodeJpaEntity();
        entity.setId(domain.getId());
        entity.setEpisodeNumber(domain.getEpisodeNumber());
        entity.setTitle(domain.getTitle());
        entity.setDescription(domain.getDescription());
        entity.setDuration(domain.getDuration());
        entity.setStatus(domain.getStatus());
        entity.setRawFileKey(domain.getRawFileKey());
        entity.setMasterPlaylistKey(domain.getMasterPlaylistKey());
        entity.setResolutions(domain.getResolutions());
        entity.setThumbnailUrl(domain.getThumbnailUrl());
        entity.setAirDate(domain.getAirDate());
        entity.setIsPremium(domain.getIsPremium());
        entity.setViewCount(domain.getViewCount());
        entity.setDeletedAt(domain.getDeletedAt());
        if (domain.getMovieId() != null) {
            MovieJpaEntity movie = new MovieJpaEntity();
            movie.setId(domain.getMovieId());
            entity.setMovie(movie);
        }
        return entity;
    }

    public Episode toSimpleDomain(EpisodeJpaEntity entity) {
        if (entity == null) return null;
        return Episode.builder()
                .id(entity.getId())
                .episodeNumber(entity.getEpisodeNumber())
                .title(entity.getTitle())
                .duration(entity.getDuration())
                .status(entity.getStatus())
                .thumbnailUrl(entity.getThumbnailUrl())
                .build();
    }
}


