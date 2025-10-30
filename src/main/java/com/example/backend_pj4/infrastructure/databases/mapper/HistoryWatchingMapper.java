package com.example.backend_pj4.infrastructure.databases.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.entities.HistoryWatching;
import com.example.backend_pj4.infrastructure.databases.entities.HistoryWatchingEntity;

@Component
public class HistoryWatchingMapper {

    private final UserMapper userMapper;
    private final MovieMapper movieMapper;
    private final EpisodeMapper episodeMapper;

    public HistoryWatchingMapper(
            UserMapper userMapper,
            MovieMapper movieMapper,
            @Lazy EpisodeMapper episodeMapper // tránh vòng lặp
    ) {
        this.userMapper = userMapper;
        this.movieMapper = movieMapper;
        this.episodeMapper = episodeMapper;
    }

    // ==============================
    // Entity → Domain
    // ==============================
    public HistoryWatching toDomain(HistoryWatchingEntity entity) {
        if (entity == null) return null;

        return HistoryWatching.builder()
                .historyId(entity.getHistoryId())
                .user(userMapper.toDomain(entity.getUser()))
                .movie(movieMapper.toDomain(entity.getMovie()))
                .episode(episodeMapper.toSimpleDomain(entity.getEpisode())) // dùng bản nhẹ để tránh vòng lặp
                .watchDuration(entity.getWatchDuration())
                .totalDuration(entity.getTotalDuration())
                .progress(entity.getProgress())
                .isCompleted(entity.getIsCompleted())
                .watchedAt(entity.getWatchedAt())
                .lastPosition(entity.getLastPosition())
                .build();
    }

    // ==============================
    // Domain → Entity
    // ==============================
    public HistoryWatchingEntity toEntity(HistoryWatching domain) {
        if (domain == null) return null;

        HistoryWatchingEntity entity = new HistoryWatchingEntity();
        entity.setHistoryId(domain.getHistoryId());
        entity.setUser(userMapper.toEntity(domain.getUser()));
        entity.setMovie(movieMapper.toEntity(domain.getMovie()));
        entity.setEpisode(episodeMapper.toEntity(domain.getEpisode()));
        entity.setWatchDuration(domain.getWatchDuration());
        entity.setTotalDuration(domain.getTotalDuration());
        entity.setProgress(domain.getProgress());
        entity.setIsCompleted(domain.getIsCompleted());
        entity.setWatchedAt(domain.getWatchedAt());
        entity.setLastPosition(domain.getLastPosition());
        return entity;
    }

    // ==============================
    // List mapping
    // ==============================
    public List<HistoryWatching> toDomainList(List<HistoryWatchingEntity> entities) {
        if (entities == null || entities.isEmpty()) return List.of();
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    public List<HistoryWatchingEntity> toEntityList(List<HistoryWatching> domains) {
        if (domains == null || domains.isEmpty()) return List.of();
        return domains.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
