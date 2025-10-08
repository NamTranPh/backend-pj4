package com.example.backend_pj4.infrastructure.databases.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "history_watching",
    indexes = {
        @Index(name = "idx_user_history", columnList = "user_id"),
        @Index(name = "idx_watched_at", columnList = "watched_at")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryWatchingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Integer historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private MovieEntity movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id")
    private EpisodeEntity episode;

    @Column(name = "watch_duration", columnDefinition = "INT DEFAULT 0 COMMENT 'Thời gian đã xem (giây)'")
    private Integer watchDuration = 0;

    @Column(name = "total_duration", columnDefinition = "INT COMMENT 'Tổng thời lượng video (giây)'")
    private Integer totalDuration;

    @Column(name = "progress", precision = 5, scale = 2, columnDefinition = "DECIMAL(5,2) DEFAULT 0.00 COMMENT 'Tiến độ xem (%)'")
    private BigDecimal progress = BigDecimal.valueOf(0.00);

    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    @CreationTimestamp
    @Column(name = "watched_at")
    private LocalDateTime watchedAt;

    @Column(name = "last_position", columnDefinition = "INT DEFAULT 0 COMMENT 'Vị trí dừng xem cuối (giây)'")
    private Integer lastPosition = 0;
}