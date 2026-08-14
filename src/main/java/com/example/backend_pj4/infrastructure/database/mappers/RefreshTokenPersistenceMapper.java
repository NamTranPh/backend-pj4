package com.example.backend_pj4.infrastructure.database.mappers;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.model.RefreshToken;
import com.example.backend_pj4.infrastructure.database.entities.RefreshTokenJpaEntity;

@Component
public class RefreshTokenPersistenceMapper {

    public RefreshToken toDomain(RefreshTokenJpaEntity entity) {
        if (entity == null) return null;
        return RefreshToken.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .tokenId(entity.getTokenId())
                .admin(entity.isAdmin())
                .expiresAt(entity.getExpiresAt())
                .revokedAt(entity.getRevokedAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public RefreshTokenJpaEntity toEntity(RefreshToken domain) {
        if (domain == null) return null;
        RefreshTokenJpaEntity entity = new RefreshTokenJpaEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setTokenId(domain.getTokenId());
        entity.setAdmin(domain.isAdmin());
        entity.setExpiresAt(domain.getExpiresAt());
        entity.setRevokedAt(domain.getRevokedAt());
        return entity;
    }
}
