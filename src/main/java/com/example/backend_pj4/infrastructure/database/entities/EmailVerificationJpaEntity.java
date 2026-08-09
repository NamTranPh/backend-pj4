package com.example.backend_pj4.infrastructure.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verification")
@Getter
public class EmailVerificationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;

    @Column(name = "token", unique = true, nullable = false, length = 255)
    private String token;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public EmailVerificationJpaEntity() {}

    public void setId(String id) { this.id = id; }

    public EmailVerificationJpaEntity(String id, UserJpaEntity user, String token,
                                   LocalDateTime expiresAt, LocalDateTime usedAt,
                                   LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.token = token;
        this.expiresAt = expiresAt;
        this.usedAt = usedAt;
        this.createdAt = createdAt;
    }

    public void setUser(UserJpaEntity user) { this.user = user; }
    public void setToken(String token) { this.token = token; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public void setUsedAt(LocalDateTime usedAt) { this.usedAt = usedAt; }
}

