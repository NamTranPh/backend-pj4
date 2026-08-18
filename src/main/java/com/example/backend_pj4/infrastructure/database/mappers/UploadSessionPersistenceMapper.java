package com.example.backend_pj4.infrastructure.database.mappers;

import java.util.Collections;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.model.UploadPart;
import com.example.backend_pj4.domain.model.UploadSession;
import com.example.backend_pj4.infrastructure.database.entities.UploadPartJpaEntity;
import com.example.backend_pj4.infrastructure.database.entities.UploadSessionJpaEntity;

@Component
public class UploadSessionPersistenceMapper {

    public UploadSession toDomain(UploadSessionJpaEntity entity) {
        if (entity == null) return null;
        return UploadSession.builder()
                .id(entity.getId())
                .targetId(entity.getTargetId())
                .targetType(entity.getTargetType())
                .rawFileKey(entity.getRawFileKey())
                .uploadId(entity.getUploadId())
                .fileSizeBytes(entity.getFileSizeBytes())
                .fileName(entity.getFileName())
                .status(entity.getStatus())
                .expiresAt(entity.getExpiresAt())
                .createdAt(entity.getCreatedAt())
                .parts(entity.getParts() != null
                        ? entity.getParts().stream().map(this::toPartDomain).toList()
                        : Collections.emptyList())
                .build();
    }

    public UploadSessionJpaEntity toEntity(UploadSession domain) {
        if (domain == null) return null;
        UploadSessionJpaEntity entity = new UploadSessionJpaEntity();
        entity.setId(domain.getId());
        entity.setTargetId(domain.getTargetId());
        entity.setTargetType(domain.getTargetType());
        entity.setRawFileKey(domain.getRawFileKey());
        entity.setUploadId(domain.getUploadId());
        entity.setFileSizeBytes(domain.getFileSizeBytes());
        entity.setFileName(domain.getFileName());
        entity.setStatus(domain.getStatus());
        entity.setExpiresAt(domain.getExpiresAt());
        return entity;
    }

    private UploadPart toPartDomain(UploadPartJpaEntity entity) {
        return UploadPart.builder()
                .id(entity.getId())
                .partNumber(entity.getPartNumber())
                .etag(entity.getEtag())
                .sizeBytes(entity.getSizeBytes())
                .uploadedAt(entity.getUploadedAt())
                .build();
    }
}
