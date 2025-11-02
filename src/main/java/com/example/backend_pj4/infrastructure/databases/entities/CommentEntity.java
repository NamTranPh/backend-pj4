package com.example.backend_pj4.infrastructure.databases.entities;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comment", indexes = {
        @Index(name = "idx_movie_comment", columnList = "movie_id"),
        @Index(name = "idx_episode_comment", columnList = "episode_id"),
        @Index(name = "idx_user_comment", columnList = "user_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "comment_id", updatable = false, nullable = false)
    private String commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private MovieEntity movie;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "episode_id", columnDefinition = "INT NULL COMMENT 'NULL
    // nếu bình luận cho phim lẻ'")
    // private EpisodeEntity episode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id", columnDefinition = "VARCHAR(36) NULL COMMENT 'NULL nếu bình luận cho phim lẻ'")
    private EpisodeEntity episode;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "parent_id", columnDefinition = "INT NULL COMMENT 'ID bình
    // luận cha (reply)'")
    // private CommentEntity parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "comment_id", columnDefinition = "VARCHAR(36) NULL COMMENT 'ID bình luận cha (reply)'")
    private CommentEntity parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<CommentEntity> replies;

    @Column(name = "is_approved")
    private Boolean isApproved = false;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}