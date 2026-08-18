package com.example.backend_pj4.infrastructure.database.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "upload_session", indexes = {
        @Index(name = "idx_upload_session_target", columnList = "target_id, target_type"),
        @Index(name = "idx_upload_session_status_expires", columnList = "status, expires_at")
})
@Getter
public class UploadSessionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "target_id", nullable = false, length = 36)
    private String targetId;

    @Column(name = "target_type", nullable = false, length = 20)
    private String targetType;

    @Column(name = "raw_file_key", nullable = false, length = 500)
    private String rawFileKey;

    @Column(name = "upload_id", nullable = false, length = 512)
    private String uploadId;

    @Column(name = "file_size_bytes", nullable = false)
    private long fileSizeBytes;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<UploadPartJpaEntity> parts = new ArrayList<>();

    public UploadSessionJpaEntity() {}

    public void setId(String id) { this.id = id; }
    public void setTargetId(String targetId) { this.targetId = targetId; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public void setRawFileKey(String rawFileKey) { this.rawFileKey = rawFileKey; }
    public void setUploadId(String uploadId) { this.uploadId = uploadId; }
    public void setFileSizeBytes(long fileSizeBytes) { this.fileSizeBytes = fileSizeBytes; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setStatus(String status) { this.status = status; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public void setParts(List<UploadPartJpaEntity> parts) { this.parts = parts; }
}
