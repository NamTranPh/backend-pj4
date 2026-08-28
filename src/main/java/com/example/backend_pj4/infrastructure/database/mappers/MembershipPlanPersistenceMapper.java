package com.example.backend_pj4.infrastructure.database.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.model.MembershipPlan;
import com.example.backend_pj4.infrastructure.database.entities.MembershipPlanJpaEntity;

@Component
public class MembershipPlanPersistenceMapper {

    public MembershipPlan toDomain(MembershipPlanJpaEntity entity) {
        if (entity == null) return null;

        return MembershipPlan.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .price(entity.getPrice())
                .durationDays(entity.getDurationDays())
                .maxDevices(entity.getMaxDevices())
                .canDownload(entity.getCanDownload())
                .videoQuality(entity.getVideoQuality())
                .description(entity.getDescription())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public MembershipPlanJpaEntity toEntity(MembershipPlan domain) {
        if (domain == null) return null;

        MembershipPlanJpaEntity entity = new MembershipPlanJpaEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setSlug(domain.getSlug());
        entity.setPrice(domain.getPrice());
        entity.setDurationDays(domain.getDurationDays());
        entity.setMaxDevices(domain.getMaxDevices());
        entity.setCanDownload(domain.getCanDownload());
        entity.setVideoQuality(domain.getVideoQuality());
        entity.setDescription(domain.getDescription());
        entity.setIsActive(domain.getIsActive());
        return entity;
    }

    public List<MembershipPlan> toDomainList(List<MembershipPlanJpaEntity> entities) {
        if (entities == null || entities.isEmpty()) return List.of();
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    public List<MembershipPlanJpaEntity> toEntityList(List<MembershipPlan> domains) {
        if (domains == null || domains.isEmpty()) return List.of();
        return domains.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}


