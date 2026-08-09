package com.example.backend_pj4.infrastructure.database.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.model.Membership;
import com.example.backend_pj4.infrastructure.database.entities.MembershipJpaEntity;
import com.example.backend_pj4.infrastructure.database.entities.MembershipPlanJpaEntity;
import com.example.backend_pj4.infrastructure.database.entities.UserJpaEntity;

@Component
public class MembershipPersistenceMapper {

    public Membership toDomain(MembershipJpaEntity entity) {
        if (entity == null) return null;

        return Membership.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .membershipPlanId(entity.getPlan() != null ? entity.getPlan().getId() : null)
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .paymentStatus(entity.getPaymentStatus())
                .autoRenewal(entity.getAutoRenewal())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public MembershipJpaEntity toEntity(Membership domain) {
        if (domain == null) return null;

        MembershipJpaEntity entity = new MembershipJpaEntity();
        entity.setId(domain.getId());
        entity.setStartDate(domain.getStartDate());
        entity.setEndDate(domain.getEndDate());
        entity.setPaymentStatus(domain.getPaymentStatus());
        entity.setAutoRenewal(domain.getAutoRenewal());
        entity.setIsActive(domain.getIsActive());
        
        if (domain.getUserId() != null) {
            UserJpaEntity user = new UserJpaEntity();
            user.setId(domain.getUserId());
            entity.setUser(user);
        }
        if (domain.getMembershipPlanId() != null) {
            MembershipPlanJpaEntity plan = new MembershipPlanJpaEntity();
            plan.setId(domain.getMembershipPlanId());
            entity.setPlan(plan);
        }
        
        return entity;
    }

    public List<Membership> toDomainList(List<MembershipJpaEntity> entities) {
        if (entities == null || entities.isEmpty()) return List.of();
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    public List<MembershipJpaEntity> toEntityList(List<Membership> domains) {
        if (domains == null || domains.isEmpty()) return List.of();
        return domains.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public Membership toSimpleDomain(MembershipJpaEntity entity) {
        if (entity == null) return null;
        return Membership.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .membershipPlanId(entity.getPlan() != null ? entity.getPlan().getId() : null)
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .paymentStatus(entity.getPaymentStatus())
                .isActive(entity.getIsActive())
                .build();
    }
}
