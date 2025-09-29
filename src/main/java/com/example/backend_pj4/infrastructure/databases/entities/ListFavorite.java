package com.example.backend_pj4.infrastructure.databases.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

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
public class ListFavorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Integer favoriteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @CreationTimestamp
    @Column(name = "added_at")
    private LocalDateTime addedAt;
}