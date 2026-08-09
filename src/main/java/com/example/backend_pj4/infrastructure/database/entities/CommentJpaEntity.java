package com.example.backend_pj4.infrastructure.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comment", indexes = {
        @Index(name = "idx_comment_user_id", columnList = "user_id"),
        @Index(name = "idx_comment_movie_id", columnList = "movie_id"),
        @Index(name = "idx_comment_episode_id", columnList = "episode_id"),
        @Index(name = "idx_comment_deleted_at", columnList = "deleted_at")
})
@Where(clause = "deleted_at IS NULL")
@Getter
public class CommentJpaEntity {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id")
    private EpisodeJpaEntity episode;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CommentJpaEntity parent;

    @Column(name = "is_approved")
    private Boolean isApproved;

    @Column(name = "like_count")
    private Integer likeCount;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<CommentJpaEntity> replies = new ArrayList<>();

    public CommentJpaEntity() {}

    public void setId(String id) { this.id = id; }

    public CommentJpaEntity(String id, UserJpaEntity user, MovieJpaEntity movie, EpisodeJpaEntity episode,
                         String content, CommentJpaEntity parent, Boolean isApproved, Integer likeCount,
                         LocalDateTime deletedAt, LocalDateTime createdAt, LocalDateTime updatedAt,
                         List<CommentJpaEntity> replies) {
        this.id = id;
        this.user = user;
        this.movie = movie;
        this.episode = episode;
        this.content = content;
        this.parent = parent;
        this.isApproved = isApproved;
        this.likeCount = likeCount;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.replies = replies != null ? replies : new ArrayList<>();
    }

    public void setUser(UserJpaEntity user) { this.user = user; }
    public void setMovie(MovieJpaEntity movie) { this.movie = movie; }
    public void setEpisode(EpisodeJpaEntity episode) { this.episode = episode; }
    public void setContent(String content) { this.content = content; }
    public void setParent(CommentJpaEntity parent) { this.parent = parent; }
    public void setIsApproved(Boolean isApproved) { this.isApproved = isApproved; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}

