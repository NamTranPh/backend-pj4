package com.example.backend_pj4.infrastructure.databases.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.backend_pj4.domain.enums.MovieStatus;
import com.example.backend_pj4.domain.enums.MovieType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "movie", indexes = {
        @Index(name = "idx_movie_type", columnList = "movie_type"),
        @Index(name = "idx_release_year", columnList = "release_year"),
        @Index(name = "idx_rating", columnList = "rating")
})

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "movie_id", updatable = false, nullable = false)
    private String movieId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "original_title")
    private String originalTitle;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "release_year", columnDefinition = "YEAR")
    private Integer releaseYear;

    @Column(name = "duration", columnDefinition = "INT COMMENT 'Thời lượng tính bằng phút'")
    private Integer duration;

    @Column(name = "director")
    private String director;

    @Column(name = "actors", columnDefinition = "TEXT")
    private String actors;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "language", length = 50)
    private String language;

    @Column(name = "trailer_url", length = 500)
    private String trailerUrl;

    @Column(name = "poster_url", length = 500)
    private String posterUrl;

    @Column(name = "backdrop_url", length = 500)
    private String backdropUrl;

    @Column(name = "rating", precision = 3, scale = 1)
    private BigDecimal rating = BigDecimal.valueOf(0.0);

    @Column(name = "view_count")
    private Integer viewCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "movie_type", nullable = false)
    private MovieType movieType;

    @Column(name = "total_episodes", columnDefinition = "INT DEFAULT 1 COMMENT 'Tổng số tập (phim bộ)'")
    private Integer totalEpisodes = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MovieStatus status = MovieStatus.COMPLETED;

    @Column(name = "is_premium")
    private Boolean isPremium = false;

    @Column(name = "is_featured")
    private Boolean isFeatured = false;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private UserEntity createdBy;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "movie_genre", joinColumns = @JoinColumn(name = "movie_id"), inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private List<GenreEntity> genres;

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<EpisodeEntity> episodes;

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CommentEntity> comments;

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RatingEntity> ratings;

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ListFavoriteEntity> favorites;

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<HistoryWatchingEntity> watchingHistory;
}
