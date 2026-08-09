package com.example.backend_pj4.infrastructure.database.entities;

import com.example.backend_pj4.common.constants.enums.ReportReason;
import com.example.backend_pj4.common.constants.enums.ReportStatus;
import com.example.backend_pj4.common.constants.enums.ReportTargetType;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name = "report", indexes = {
        @Index(name = "idx_report_reporter_id", columnList = "reporter_id"),
        @Index(name = "idx_report_target_type", columnList = "target_type"),
        @Index(name = "idx_report_status", columnList = "status"),
        @Index(name = "idx_report_deleted_at", columnList = "deleted_at")
})
@Where(clause = "deleted_at IS NULL")
@Getter
public class ReportJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private UserJpaEntity reporter;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private ReportTargetType targetType;

    @Column(name = "target_id", nullable = false, length = 36)
    private String targetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    private ReportReason reason;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ReportStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by")
    private UserJpaEntity resolvedBy;

    @Column(name = "admin_note", columnDefinition = "TEXT")
    private String adminNote;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public ReportJpaEntity() {}

    public ReportJpaEntity(String id, UserJpaEntity reporter, ReportTargetType targetType, String targetId,
                        ReportReason reason, String description, ReportStatus status,
                        UserJpaEntity resolvedBy, String adminNote, LocalDateTime resolvedAt,
                        LocalDateTime deletedAt, LocalDateTime createdAt) {
        this.id = id;
        this.reporter = reporter;
        this.targetType = targetType;
        this.targetId = targetId;
        this.reason = reason;
        this.description = description;
        this.status = status;
        this.resolvedBy = resolvedBy;
        this.adminNote = adminNote;
        this.resolvedAt = resolvedAt;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
    }

    public void setReporter(UserJpaEntity reporter) { this.reporter = reporter; }
    public void setTargetType(ReportTargetType targetType) { this.targetType = targetType; }
    public void setTargetId(String targetId) { this.targetId = targetId; }
    public void setReason(ReportReason reason) { this.reason = reason; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(ReportStatus status) { this.status = status; }
    public void setResolvedBy(UserJpaEntity resolvedBy) { this.resolvedBy = resolvedBy; }
    public void setAdminNote(String adminNote) { this.adminNote = adminNote; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    public void setId(String id) { this.id = id; }
}

