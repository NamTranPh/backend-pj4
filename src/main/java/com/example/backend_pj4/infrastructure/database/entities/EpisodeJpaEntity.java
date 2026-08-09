package com.example.backend_pj4.infrastructure.database.entities;

import com.example.backend_pj4.common.constants.enums.VideoStatus;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "episode",
        uniqueConstraints = @UniqueConstraint(columnNames = {"movie_id", "episode_number"}),
        indexes = {
                @Index(name = "idx_episode_episode_number", columnList = "episode_number"),
                @Index(name = "idx_episode_status", columnList = "status"),
                @Index(name = "idx_episode_deleted_at", columnList = "deleted_at")
        })
@Where(clause = "deleted_at IS NULL")
@Getter
public class EpisodeJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private MovieJpaEntity movie;

    @Column(name = "episode_number", nullable = false)
    private Integer episodeNumber;

    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "duration")
    private Integer duration;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VideoStatus status;

    @Column(name = "raw_file_key", length = 500)
    private String rawFileKey;

    @Column(name = "master_playlist_key", length = 500)
    private String masterPlaylistKey;

    @Column(name = "resolutions", columnDefinition = "JSON")
    private String resolutions;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(name = "air_date")
    private LocalDate airDate;

    @Column(name = "is_premium")
    private Boolean isPremium;

    @Column(name = "view_count")
    private Long viewCount;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "episode", fetch = FetchType.LAZY)
    private List<CommentJpaEntity> comments = new ArrayList<>();

    @OneToMany(mappedBy = "episode", fetch = FetchType.LAZY)
    private List<HistoryWatchingJpaEntity> watchingHistory = new ArrayList<>();

    public EpisodeJpaEntity() {}

    public void setId(String id) { this.id = id; }

    public EpisodeJpaEntity(String id, MovieJpaEntity movie, Integer episodeNumber, String title,
                         String description, Integer duration, VideoStatus status,
                         String rawFileKey, String masterPlaylistKey, String resolutions,
                         String thumbnailUrl, LocalDate airDate, Boolean isPremium,
                         Long viewCount, LocalDateTime deletedAt, LocalDateTime createdAt,
                         List<CommentJpaEntity> comments, List<HistoryWatchingJpaEntity> watchingHistory) {
        this.id = id;
        this.movie = movie;
        this.episodeNumber = episodeNumber;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.status = status;
        this.rawFileKey = rawFileKey;
        this.masterPlaylistKey = masterPlaylistKey;
        this.resolutions = resolutions;
        this.thumbnailUrl = thumbnailUrl;
        this.airDate = airDate;
        this.isPremium = isPremium;
        this.viewCount = viewCount;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.comments = comments != null ? comments : new ArrayList<>();
        this.watchingHistory = watchingHistory != null ? watchingHistory : new ArrayList<>();
    }

    public void setMovie(MovieJpaEntity movie) { this.movie = movie; }
    public void setEpisodeNumber(Integer episodeNumber) { this.episodeNumber = episodeNumber; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public void setStatus(VideoStatus status) { this.status = status; }
    public void setRawFileKey(String rawFileKey) { this.rawFileKey = rawFileKey; }
    public void setMasterPlaylistKey(String masterPlaylistKey) { this.masterPlaylistKey = masterPlaylistKey; }
    public void setResolutions(String resolutions) { this.resolutions = resolutions; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public void setAirDate(LocalDate airDate) { this.airDate = airDate; }
    public void setIsPremium(Boolean isPremium) { this.isPremium = isPremium; }
    public void setViewCount(Long viewCount) { this.viewCount = viewCount; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}

