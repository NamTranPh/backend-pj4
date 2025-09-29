package com.example.backend_pj4.infrastructure.databases.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "rating",
    uniqueConstraints = {
        @UniqueConstraint(name = "unique_user_movie_rating", columnNames = {"user_id", "movie_id"})
    },
    indexes = {
        @Index(name = "idx_movie_rating", columnList = "movie_id"),
        @Index(name = "idx_score", columnList = "score")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    private Integer ratingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(name = "score", nullable = false, columnDefinition = "INT CHECK (score BETWEEN 1 AND 10)")
    private Integer score;

    @Column(name = "review", columnDefinition = "TEXT")
    private String review;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}