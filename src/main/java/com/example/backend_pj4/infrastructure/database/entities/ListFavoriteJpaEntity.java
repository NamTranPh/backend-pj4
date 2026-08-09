package com.example.backend_pj4.infrastructure.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name = "list_favorite",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "movie_id"}),
        indexes = {
                @Index(name = "idx_list_favorite_user_id", columnList = "user_id"),
                @Index(name = "idx_list_favorite_deleted_at", columnList = "deleted_at")
        })
@Where(clause = "deleted_at IS NULL")
@Getter
public class ListFavoriteJpaEntity {

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

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public ListFavoriteJpaEntity() {}

    public void setId(String id) { this.id = id; }

    public ListFavoriteJpaEntity(String id, UserJpaEntity user, MovieJpaEntity movie,
                              LocalDateTime deletedAt, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.movie = movie;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
    }

    public void setUser(UserJpaEntity user) { this.user = user; }
    public void setMovie(MovieJpaEntity movie) { this.movie = movie; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}

