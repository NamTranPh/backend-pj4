package com.example.backend_pj4.infrastructure.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_token", indexes = {
        @Index(name = "idx_refresh_token_token_id", columnList = "token_id", unique = true),
        @Index(name = "idx_refresh_token_user_id", columnList = "user_id")
})
@Getter
public class RefreshTokenJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "token_id", nullable = false, unique = true)
    private String tokenId;

    @Column(name = "is_admin", nullable = false)
    private boolean admin;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public RefreshTokenJpaEntity() {}

    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setTokenId(String tokenId) { this.tokenId = tokenId; }
    public void setAdmin(boolean admin) { this.admin = admin; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public void setRevokedAt(LocalDateTime revokedAt) { this.revokedAt = revokedAt; }
}
