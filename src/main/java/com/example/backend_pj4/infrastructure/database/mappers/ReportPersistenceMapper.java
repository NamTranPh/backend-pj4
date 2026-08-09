package com.example.backend_pj4.infrastructure.database.mappers;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.model.Report;
import com.example.backend_pj4.infrastructure.database.entities.ReportJpaEntity;
import com.example.backend_pj4.infrastructure.database.entities.UserJpaEntity;

@Component
public class ReportPersistenceMapper {

    public Report toDomain(ReportJpaEntity entity) {
        if (entity == null) return null;
        return Report.builder()
                .id(entity.getId())
                .reporterId(entity.getReporter() != null ? entity.getReporter().getId() : null)
                .targetType(entity.getTargetType())
                .targetId(entity.getTargetId())
                .reason(entity.getReason())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .resolvedBy(entity.getResolvedBy() != null ? entity.getResolvedBy().getId() : null)
                .adminNote(entity.getAdminNote())
                .resolvedAt(entity.getResolvedAt())
                .deletedAt(entity.getDeletedAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public ReportJpaEntity toEntity(Report domain) {
        if (domain == null) return null;
        ReportJpaEntity entity = new ReportJpaEntity();
        entity.setId(domain.getId());
        entity.setTargetType(domain.getTargetType());
        entity.setTargetId(domain.getTargetId());
        entity.setReason(domain.getReason());
        entity.setDescription(domain.getDescription());
        entity.setStatus(domain.getStatus());
        entity.setAdminNote(domain.getAdminNote());
        entity.setResolvedAt(domain.getResolvedAt());
        entity.setDeletedAt(domain.getDeletedAt());
        if (domain.getReporterId() != null) {
            UserJpaEntity reporter = new UserJpaEntity();
            reporter.setId(domain.getReporterId());
            entity.setReporter(reporter);
        }
        if (domain.getResolvedBy() != null) {
            UserJpaEntity resolver = new UserJpaEntity();
            resolver.setId(domain.getResolvedBy());
            entity.setResolvedBy(resolver);
        }
        return entity;
    }
}


