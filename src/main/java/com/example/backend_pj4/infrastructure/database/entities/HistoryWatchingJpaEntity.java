package com.example.backend_pj4.infrastructure.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "history_watching", indexes = {
        @Index(name = "idx_history_watching_user_id", columnList = "user_id"),
        @Index(name = "idx_history_watching_watched_at", columnList = "watched_at")
})
@Getter
public class HistoryWatchingJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private MovieJpaEntity movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id")
    private EpisodeJpaEntity episode;

    @Column(name = "watch_duration")
    private Integer watchDuration;

    @Column(name = "total_duration")
    private Integer totalDuration;

    @Column(name = "progress", precision = 5, scale = 2)
    private BigDecimal progress;

    @Column(name = "is_completed")
    private Boolean isCompleted;

    @Column(name = "last_position")
    private Integer lastPosition;

    @Column(name = "watched_at")
    private LocalDateTime watchedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public HistoryWatchingJpaEntity() {}

    public void setId(String id) { this.id = id; }

    public HistoryWatchingJpaEntity(String id, UserJpaEntity user, MovieJpaEntity movie, EpisodeJpaEntity episode,
                                 Integer watchDuration, Integer totalDuration, BigDecimal progress,
                                 Boolean isCompleted, Integer lastPosition, LocalDateTime watchedAt,
                                 LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.movie = movie;
        this.episode = episode;
        this.watchDuration = watchDuration;
        this.totalDuration = totalDuration;
        this.progress = progress;
        this.isCompleted = isCompleted;
        this.lastPosition = lastPosition;
        this.watchedAt = watchedAt;
        this.createdAt = createdAt;
    }

    public void setUser(UserJpaEntity user) { this.user = user; }
    public void setMovie(MovieJpaEntity movie) { this.movie = movie; }
    public void setEpisode(EpisodeJpaEntity episode) { this.episode = episode; }
    public void setWatchDuration(Integer watchDuration) { this.watchDuration = watchDuration; }
    public void setTotalDuration(Integer totalDuration) { this.totalDuration = totalDuration; }
    public void setProgress(BigDecimal progress) { this.progress = progress; }
    public void setIsCompleted(Boolean isCompleted) { this.isCompleted = isCompleted; }
    public void setLastPosition(Integer lastPosition) { this.lastPosition = lastPosition; }
    public void setWatchedAt(LocalDateTime watchedAt) { this.watchedAt = watchedAt; }
}

