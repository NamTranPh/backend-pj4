package com.example.backend_pj4.infrastructure.database.mappers;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.model.EmailVerification;
import com.example.backend_pj4.infrastructure.database.entities.EmailVerificationJpaEntity;
import com.example.backend_pj4.infrastructure.database.entities.UserJpaEntity;

@Component
public class EmailVerificationPersistenceMapper {

    public EmailVerification toDomain(EmailVerificationJpaEntity entity) {
        if (entity == null) return null;
        return EmailVerification.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .token(entity.getToken())
                .expiresAt(entity.getExpiresAt())
                .usedAt(entity.getUsedAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public EmailVerificationJpaEntity toEntity(EmailVerification domain) {
        if (domain == null) return null;
        EmailVerificationJpaEntity entity = new EmailVerificationJpaEntity();
        entity.setId(domain.getId());
        entity.setToken(domain.getToken());
        entity.setExpiresAt(domain.getExpiresAt());
        entity.setUsedAt(domain.getUsedAt());
        if (domain.getUserId() != null) {
            UserJpaEntity user = new UserJpaEntity();
            user.setId(domain.getUserId());
            entity.setUser(user);
        }
        return entity;
    }
}


