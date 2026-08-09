package com.example.backend_pj4.infrastructure.database.entities;

import com.example.backend_pj4.common.constants.enums.MovieType;
import com.example.backend_pj4.common.constants.enums.VideoStatus;
import com.example.backend_pj4.common.constants.enums.VideoVisibility;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movie", indexes = {
        @Index(name = "idx_movie_movie_type", columnList = "movie_type"),
        @Index(name = "idx_movie_release_year", columnList = "release_year"),
        @Index(name = "idx_movie_rating", columnList = "rating"),
        @Index(name = "idx_movie_status", columnList = "status"),
        @Index(name = "idx_movie_deleted_at", columnList = "deleted_at")
})
@Where(clause = "deleted_at IS NULL")
@Getter
public class MovieJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "original_title")
    private String originalTitle;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "release_year", columnDefinition = "YEAR")
    private Integer releaseYear;

    @Column(name = "duration")
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

    @Enumerated(EnumType.STRING)
    @Column(name = "movie_type", nullable = false)
    private MovieType movieType;

    @Column(name = "total_episodes")
    private Integer totalEpisodes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VideoStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private VideoVisibility visibility;

    @Column(name = "is_premium")
    private Boolean isPremium;

    @Column(name = "is_featured")
    private Boolean isFeatured;

    @Column(name = "rating", precision = 3, scale = 1)
    private BigDecimal rating;

    @Column(name = "view_count")
    private Long viewCount;

    @Column(name = "raw_file_key", length = 500)
    private String rawFileKey;

    @Column(name = "master_playlist_key", length = 500)
    private String masterPlaylistKey;

    @Column(name = "resolutions", columnDefinition = "JSON")
    private String resolutions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private UserJpaEntity createdBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "movie_genre",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private List<GenreJpaEntity> genres = new ArrayList<>();

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<EpisodeJpaEntity> episodes = new ArrayList<>();

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    private List<CommentJpaEntity> comments = new ArrayList<>();

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    private List<RatingJpaEntity> ratings = new ArrayList<>();

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    private List<ListFavoriteJpaEntity> favorites = new ArrayList<>();

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    private List<HistoryWatchingJpaEntity> watchingHistory = new ArrayList<>();

    public MovieJpaEntity() {}

    public void setId(String id) { this.id = id; }

    public MovieJpaEntity(String id, String title, String originalTitle, String description,
                       Integer releaseYear, Integer duration, String director, String actors,
                       String country, String language, String trailerUrl, String posterUrl,
                       String backdropUrl, MovieType movieType, Integer totalEpisodes,
                       VideoStatus status, VideoVisibility visibility, Boolean isPremium,
                       Boolean isFeatured, BigDecimal rating, Long viewCount,
                       String rawFileKey, String masterPlaylistKey, String resolutions,
                       UserJpaEntity createdBy, LocalDateTime deletedAt,
                       LocalDateTime createdAt, LocalDateTime updatedAt,
                       List<GenreJpaEntity> genres, List<EpisodeJpaEntity> episodes,
                       List<CommentJpaEntity> comments, List<RatingJpaEntity> ratings,
                       List<ListFavoriteJpaEntity> favorites, List<HistoryWatchingJpaEntity> watchingHistory) {
        this.id = id;
        this.title = title;
        this.originalTitle = originalTitle;
        this.description = description;
        this.releaseYear = releaseYear;
        this.duration = duration;
        this.director = director;
        this.actors = actors;
        this.country = country;
        this.language = language;
        this.trailerUrl = trailerUrl;
        this.posterUrl = posterUrl;
        this.backdropUrl = backdropUrl;
        this.movieType = movieType;
        this.totalEpisodes = totalEpisodes;
        this.status = status;
        this.visibility = visibility;
        this.isPremium = isPremium;
        this.isFeatured = isFeatured;
        this.rating = rating;
        this.viewCount = viewCount;
        this.rawFileKey = rawFileKey;
        this.masterPlaylistKey = masterPlaylistKey;
        this.resolutions = resolutions;
        this.createdBy = createdBy;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.genres = genres != null ? genres : new ArrayList<>();
        this.episodes = episodes != null ? episodes : new ArrayList<>();
        this.comments = comments != null ? comments : new ArrayList<>();
        this.ratings = ratings != null ? ratings : new ArrayList<>();
        this.favorites = favorites != null ? favorites : new ArrayList<>();
        this.watchingHistory = watchingHistory != null ? watchingHistory : new ArrayList<>();
    }

    public void setTitle(String title) { this.title = title; }
    public void setOriginalTitle(String originalTitle) { this.originalTitle = originalTitle; }
    public void setDescription(String description) { this.description = description; }
    public void setReleaseYear(Integer releaseYear) { this.releaseYear = releaseYear; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public void setDirector(String director) { this.director = director; }
    public void setActors(String actors) { this.actors = actors; }
    public void setCountry(String country) { this.country = country; }
    public void setLanguage(String language) { this.language = language; }
    public void setTrailerUrl(String trailerUrl) { this.trailerUrl = trailerUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
    public void setBackdropUrl(String backdropUrl) { this.backdropUrl = backdropUrl; }
    public void setMovieType(MovieType movieType) { this.movieType = movieType; }
    public void setTotalEpisodes(Integer totalEpisodes) { this.totalEpisodes = totalEpisodes; }
    public void setStatus(VideoStatus status) { this.status = status; }
    public void setVisibility(VideoVisibility visibility) { this.visibility = visibility; }
    public void setIsPremium(Boolean isPremium) { this.isPremium = isPremium; }
    public void setIsFeatured(Boolean isFeatured) { this.isFeatured = isFeatured; }
    public void setRating(BigDecimal rating) { this.rating = rating; }
    public void setViewCount(Long viewCount) { this.viewCount = viewCount; }
    public void setRawFileKey(String rawFileKey) { this.rawFileKey = rawFileKey; }
    public void setMasterPlaylistKey(String masterPlaylistKey) { this.masterPlaylistKey = masterPlaylistKey; }
    public void setResolutions(String resolutions) { this.resolutions = resolutions; }
    public void setCreatedBy(UserJpaEntity createdBy) { this.createdBy = createdBy; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    public void setGenres(List<GenreJpaEntity> genres) { this.genres = genres; }
}

