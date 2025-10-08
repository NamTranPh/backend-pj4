package com.example.backend_pj4.infrastructure.databases.mapper;

import com.example.backend_pj4.domain.entities.Episode;
import com.example.backend_pj4.infrastructure.databases.entities.EpisodeEntity;

public class EpisodeMapper {

    public static Episode toDomain(EpisodeEntity entity) {
        if (entity == null)
            return null;
        return Episode.builder()
                .episodeId(entity.getEpisodeId())
                .movie(MovieMapper.toDomain(entity.getMovie()))
                .episodeNumber(entity.getEpisodeNumber())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .duration(entity.getDuration())
                .videoUrl(entity.getVideoUrl())
                .thumbnailUrl(entity.getThumbnailUrl())
                .airDate(entity.getAirDate())
                .isPremium(entity.getIsPremium())
                .viewCount(entity.getViewCount())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                // .comments(entity.getComments() != null ? entity.getComments()
                // .stream()
                // .map(CommentMapper::toDomain)
                // .collect(Collectors.toList()) : null)
                // .watchingHistory(entity.getWatchingHistory() != null ?
                // entity.getWatchingHistory()
                // .stream()
                // .map(HistoryWatchingMapper::toDomain)
                // .collect(Collectors.toList()) : null)
                .build();
    }

    public static EpisodeEntity toEntity(Episode domain) {
        if (domain == null)
            return null;
        EpisodeEntity entity = new EpisodeEntity();
        entity.setEpisodeId(domain.getEpisodeId());
        entity.setMovie(MovieMapper.toEntity(domain.getMovie()));
        entity.setEpisodeNumber(domain.getEpisodeNumber());
        entity.setTitle(domain.getTitle());
        entity.setDescription(domain.getDescription());
        entity.setDuration(domain.getDuration());
        entity.setVideoUrl(domain.getVideoUrl());
        entity.setThumbnailUrl(domain.getThumbnailUrl());
        entity.setAirDate(domain.getAirDate());
        entity.setIsPremium(domain.getIsPremium());
        entity.setViewCount(domain.getViewCount());
        entity.setIsActive(domain.getIsActive());
        entity.setCreatedAt(domain.getCreatedAt());

        return entity;
    }
}
