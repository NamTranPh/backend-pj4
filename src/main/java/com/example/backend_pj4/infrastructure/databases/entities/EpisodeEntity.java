package com.example.backend_pj4.infrastructure.databases.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "episode",
    uniqueConstraints = {
        @UniqueConstraint(name = "unique_movie_episode", columnNames = {"movie_id", "episode_number"})
    },
    indexes = {
        @Index(name = "idx_episode_number", columnList = "episode_number")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "episode_id")
    private String episodeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private MovieEntity movie;

    @Column(name = "episode_number", nullable = false)
    private Integer episodeNumber;

    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "duration", columnDefinition = "INT COMMENT 'Thời lượng tập phim'")
    private Integer duration;

    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(name = "air_date")
    private LocalDate airDate;

    @Column(name = "is_premium")
    private Boolean isPremium = false;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "episode", fetch = FetchType.LAZY)
    private List<CommentEntity> comments;

    @OneToMany(mappedBy = "episode", fetch = FetchType.LAZY)
    private List<HistoryWatchingEntity> watchingHistory;
}