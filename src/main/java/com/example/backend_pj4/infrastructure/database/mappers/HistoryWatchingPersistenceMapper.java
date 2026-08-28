package com.example.backend_pj4.infrastructure.database.mappers;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.model.HistoryWatching;
import com.example.backend_pj4.infrastructure.database.entities.EpisodeJpaEntity;
import com.example.backend_pj4.infrastructure.database.entities.HistoryWatchingJpaEntity;
import com.example.backend_pj4.infrastructure.database.entities.MovieJpaEntity;
import com.example.backend_pj4.infrastructure.database.entities.UserJpaEntity;

@Component
public class HistoryWatchingPersistenceMapper {

    public HistoryWatching toDomain(HistoryWatchingJpaEntity entity) {
        if (entity == null) return null;
        return HistoryWatching.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .movieId(entity.getMovie() != null ? entity.getMovie().getId() : null)
                .episodeId(entity.getEpisode() != null ? entity.getEpisode().getId() : null)
                .watchDuration(entity.getWatchDuration())
                .totalDuration(entity.getTotalDuration())
                .progress(entity.getProgress())
                .isCompleted(entity.getIsCompleted())
                .lastPosition(entity.getLastPosition())
                .watchedAt(entity.getWatchedAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public HistoryWatchingJpaEntity toEntity(HistoryWatching domain) {
        if (domain == null) return null;
        HistoryWatchingJpaEntity entity = new HistoryWatchingJpaEntity();
        entity.setId(domain.getId());
        entity.setWatchDuration(domain.getWatchDuration());
        entity.setTotalDuration(domain.getTotalDuration());
        entity.setProgress(domain.getProgress());
        entity.setIsCompleted(domain.getIsCompleted());
        entity.setLastPosition(domain.getLastPosition());
        entity.setWatchedAt(domain.getWatchedAt());
        if (domain.getUserId() != null) {
            UserJpaEntity user = new UserJpaEntity();
            user.setId(domain.getUserId());
            entity.setUser(user);
        }
        if (domain.getMovieId() != null) {
            MovieJpaEntity movie = new MovieJpaEntity();
            movie.setId(domain.getMovieId());
            entity.setMovie(movie);
        }
        if (domain.getEpisodeId() != null && !domain.getEpisodeId().isBlank()) {
            EpisodeJpaEntity episode = new EpisodeJpaEntity();
            episode.setId(domain.getEpisodeId());
            entity.setEpisode(episode);
        }
        return entity;
    }
}


