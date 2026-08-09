package com.example.backend_pj4.infrastructure.database.entities;

import com.example.backend_pj4.common.constants.enums.OtpType;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp_verification", indexes = {
        @Index(name = "idx_otp_email", columnList = "email"),
        @Index(name = "idx_otp_expires_at", columnList = "expires_at")
})
@Getter
public class OtpVerificationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "code_hash", nullable = false, length = 255)
    private String codeHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private OtpType type;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public OtpVerificationJpaEntity() {}

    public void setId(String id) { this.id = id; }

    public OtpVerificationJpaEntity(String id, String email, String codeHash, OtpType type,
                                 LocalDateTime expiresAt, LocalDateTime usedAt,
                                 LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.codeHash = codeHash;
        this.type = type;
        this.expiresAt = expiresAt;
        this.usedAt = usedAt;
        this.createdAt = createdAt;
    }

    public void setEmail(String email) { this.email = email; }
    public void setCodeHash(String codeHash) { this.codeHash = codeHash; }
    public void setType(OtpType type) { this.type = type; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public void setUsedAt(LocalDateTime usedAt) { this.usedAt = usedAt; }
}

