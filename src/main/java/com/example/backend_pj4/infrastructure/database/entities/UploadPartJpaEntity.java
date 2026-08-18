package com.example.backend_pj4.infrastructure.database.entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;

@Entity
@Table(name = "upload_part", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"session_id", "part_number"})
})
@Getter
public class UploadPartJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private UploadSessionJpaEntity session;

    @Column(name = "part_number", nullable = false)
    private int partNumber;

    @Column(name = "etag", nullable = false, length = 255)
    private String etag;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    @CreationTimestamp
    @Column(name = "uploaded_at", updatable = false)
    private LocalDateTime uploadedAt;

    public UploadPartJpaEntity() {}

    public void setSession(UploadSessionJpaEntity session) { this.session = session; }
    public void setPartNumber(int partNumber) { this.partNumber = partNumber; }
    public void setEtag(String etag) { this.etag = etag; }
    public void setSizeBytes(long sizeBytes) { this.sizeBytes = sizeBytes; }
}
