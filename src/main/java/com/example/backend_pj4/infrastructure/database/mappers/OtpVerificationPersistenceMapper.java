package com.example.backend_pj4.infrastructure.database.mappers;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.model.OtpVerification;
import com.example.backend_pj4.infrastructure.database.entities.OtpVerificationJpaEntity;

@Component
public class OtpVerificationPersistenceMapper {

    public OtpVerification toDomain(OtpVerificationJpaEntity entity) {
        if (entity == null) return null;
        return OtpVerification.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .codeHash(entity.getCodeHash())
                .type(entity.getType())
                .expiresAt(entity.getExpiresAt())
                .usedAt(entity.getUsedAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public OtpVerificationJpaEntity toEntity(OtpVerification domain) {
        if (domain == null) return null;
        OtpVerificationJpaEntity entity = new OtpVerificationJpaEntity();
        entity.setId(domain.getId());
        entity.setEmail(domain.getEmail());
        entity.setCodeHash(domain.getCodeHash());
        entity.setType(domain.getType());
        entity.setExpiresAt(domain.getExpiresAt());
        entity.setUsedAt(domain.getUsedAt());
        return entity;
    }
}


