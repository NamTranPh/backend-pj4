package com.example.backend_pj4.infrastructure.databases.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
public class Episode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "episode_id")
    private Integer episodeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

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
    private List<Comment> comments;

    @OneToMany(mappedBy = "episode", fetch = FetchType.LAZY)
    private List<HistoryWatching> watchingHistory;
}