package com.example.backend_pj4.infrastructure.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name = "rating",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "movie_id"}),
        indexes = {
                @Index(name = "idx_rating_movie_id", columnList = "movie_id"),
                @Index(name = "idx_rating_score", columnList = "score"),
                @Index(name = "idx_rating_deleted_at", columnList = "deleted_at")
        })
@Where(clause = "deleted_at IS NULL")
@Getter
public class RatingJpaEntity {

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

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "review", columnDefinition = "TEXT")
    private String review;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public RatingJpaEntity() {}

    public void setId(String id) { this.id = id; }

    public RatingJpaEntity(String id, UserJpaEntity user, MovieJpaEntity movie, Integer score,
                        String review, LocalDateTime deletedAt, LocalDateTime createdAt,
                        LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.movie = movie;
        this.score = score;
        this.review = review;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void setUser(UserJpaEntity user) { this.user = user; }
    public void setMovie(MovieJpaEntity movie) { this.movie = movie; }
    public void setScore(Integer score) { this.score = score; }
    public void setReview(String review) { this.review = review; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}

