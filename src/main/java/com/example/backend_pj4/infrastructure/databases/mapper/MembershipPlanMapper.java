package com.example.backend_pj4.infrastructure.databases.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.entities.MembershipPlan;
import com.example.backend_pj4.infrastructure.databases.entities.MembershipPlanEntity;

@Component
public class MembershipPlanMapper {

    public MembershipPlan toDomain(MembershipPlanEntity entity) {
        if (entity == null) return null;

        return MembershipPlan.builder()
                .planId(entity.getPlanId())
                .planName(entity.getPlanName())
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

    public MembershipPlanEntity toEntity(MembershipPlan domain) {
        if (domain == null) return null;

        MembershipPlanEntity entity = new MembershipPlanEntity();
        entity.setPlanId(domain.getPlanId());
        entity.setPlanName(domain.getPlanName());
        entity.setPrice(domain.getPrice());
        entity.setDurationDays(domain.getDurationDays());
        entity.setMaxDevices(domain.getMaxDevices());
        entity.setCanDownload(domain.getCanDownload());
        entity.setVideoQuality(domain.getVideoQuality());
        entity.setDescription(domain.getDescription());
        entity.setIsActive(domain.getIsActive());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    public List<MembershipPlan> toDomainList(List<MembershipPlanEntity> entities) {
        if (entities == null || entities.isEmpty()) return List.of();
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    public List<MembershipPlanEntity> toEntityList(List<MembershipPlan> domains) {
        if (domains == null || domains.isEmpty()) return List.of();
        return domains.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
