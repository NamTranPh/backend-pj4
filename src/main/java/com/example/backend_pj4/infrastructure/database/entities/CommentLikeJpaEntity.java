package com.example.backend_pj4.infrastructure.database.entities;

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
import lombok.Getter;

/**
 * Lượt thích một bình luận. Đây là <b>nguồn sự thật</b> của số lượt thích; cột
 * {@code comment.like_count} chỉ là ảnh chụp để trang danh sách khỏi phải {@code COUNT(*)} cho
 * từng dòng.
 */
@Entity
@Table(name = "comment_like",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_comment_like_user_comment",
                columnNames = {"user_id", "comment_id"}),
        indexes = {
                @Index(name = "idx_comment_like_comment_id", columnList = "comment_id"),
                @Index(name = "idx_comment_like_user_id", columnList = "user_id")
        })
@Getter
public class CommentLikeJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private CommentJpaEntity comment;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    protected CommentLikeJpaEntity() {
    }

    public CommentLikeJpaEntity(UserJpaEntity user, CommentJpaEntity comment) {
        this.user = user;
        this.comment = comment;
    }
}
