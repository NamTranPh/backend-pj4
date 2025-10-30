package com.example.backend_pj4.infrastructure.databases.mapper;

import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.entities.Episode;
import com.example.backend_pj4.infrastructure.databases.entities.EpisodeEntity;

@Component
public class EpisodeMapper {

    private final MovieMapper movieMapper;
    private final CommentMapper commentMapper;
    private final HistoryWatchingMapper historyWatchingMapper;

    public EpisodeMapper(
            MovieMapper movieMapper,
            @Lazy CommentMapper commentMapper, // tránh circular dependency
            HistoryWatchingMapper historyWatchingMapper) {
        this.movieMapper = movieMapper;
        this.commentMapper = commentMapper;
        this.historyWatchingMapper = historyWatchingMapper;
    }

    // ==============================
    // Entity → Domain
    // ==============================
    public Episode toDomain(EpisodeEntity entity) {
        if (entity == null)
            return null;

        return Episode.builder()
                .episodeId(entity.getEpisodeId())
                .movie(movieMapper.toDomain(entity.getMovie()))
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
                // ✅ tránh vòng lặp vô hạn Episode <-> Comment
                .comments(entity.getComments() != null
                        ? entity.getComments().stream()
                                .map(commentMapper::toSimpleDomain)
                                .collect(Collectors.toList())
                        : null)
                .watchingHistory(entity.getWatchingHistory() != null
                        ? entity.getWatchingHistory().stream()
                                .map(historyWatchingMapper::toDomain)
                                .collect(Collectors.toList())
                        : null)
                .build();
    }

    // ==============================
    // Domain → Entity
    // ==============================
    public EpisodeEntity toEntity(Episode domain) {
        if (domain == null)
            return null;

        EpisodeEntity entity = new EpisodeEntity();
        entity.setEpisodeId(domain.getEpisodeId());
        entity.setMovie(movieMapper.toEntity(domain.getMovie()));
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

    // ✅ Dành cho các mapper khác gọi mà không loop vô hạn
    public Episode toSimpleDomain(EpisodeEntity entity) {
        if (entity == null)
            return null;
        return Episode.builder()
                .episodeId(entity.getEpisodeId())
                .title(entity.getTitle())
                .episodeNumber(entity.getEpisodeNumber())
                .build();
    }
}
