package com.example.backend_pj4.infrastructure.databases.entities;

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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "list_favorite",
    uniqueConstraints = {
        @UniqueConstraint(name = "unique_user_movie_favorite", columnNames = {"user_id", "movie_id"})
    },
    indexes = {
        @Index(name = "idx_user_favorite", columnList = "user_id")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListFavoriteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "favorite_id", updatable = false, nullable = false)
    private String favoriteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private MovieEntity movie;

    @CreationTimestamp
    @Column(name = "added_at")
    private LocalDateTime addedAt;
}