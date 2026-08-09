package com.example.backend_pj4.infrastructure.database.entities;

import com.example.backend_pj4.common.constants.enums.NotificationReferenceType;
import com.example.backend_pj4.common.constants.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification", indexes = {
        @Index(name = "idx_notification_user_id", columnList = "user_id"),
        @Index(name = "idx_notification_type", columnList = "type"),
        @Index(name = "idx_notification_created_at", columnList = "created_at"),
        @Index(name = "idx_notification_deleted_at", columnList = "deleted_at")
})
@Where(clause = "deleted_at IS NULL")
@Getter
public class NotificationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "reference_type")
    private NotificationReferenceType referenceType;

    @Column(name = "reference_id", length = 36)
    private String referenceId;

    @Column(name = "is_read")
    private Boolean isRead;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public NotificationJpaEntity() {}

    public NotificationJpaEntity(String id, UserJpaEntity user, NotificationType type, String title,
                              String content, NotificationReferenceType referenceType,
                              String referenceId, Boolean isRead, LocalDateTime readAt,
                              LocalDateTime deletedAt, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.type = type;
        this.title = title;
        this.content = content;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.isRead = isRead;
        this.readAt = readAt;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
    }

    public void setUser(UserJpaEntity user) { this.user = user; }
    public void setType(NotificationType type) { this.type = type; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setReferenceType(NotificationReferenceType referenceType) { this.referenceType = referenceType; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    public void setId(String id) { this.id = id; }
}

