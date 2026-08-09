package com.example.backend_pj4.infrastructure.database.mappers;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.model.PaymentOrder;
import com.example.backend_pj4.infrastructure.database.entities.MembershipPlanJpaEntity;
import com.example.backend_pj4.infrastructure.database.entities.PaymentOrderJpaEntity;
import com.example.backend_pj4.infrastructure.database.entities.UserJpaEntity;

@Component
public class PaymentOrderPersistenceMapper {

    public PaymentOrder toDomain(PaymentOrderJpaEntity entity) {
        if (entity == null) return null;
        return PaymentOrder.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .membershipPlanId(entity.getPlan() != null ? entity.getPlan().getId() : null)
                .planName(entity.getPlanName())
                .price(entity.getPrice())
                .durationDays(entity.getDurationDays())
                .gateway(entity.getGateway())
                .status(entity.getStatus())
                .transactionCode(entity.getTransactionCode())
                .gatewayTransactionId(entity.getGatewayTransactionId())
                .checkoutUrl(entity.getCheckoutUrl())
                .failureReason(entity.getFailureReason())
                .metadata(entity.getMetadata())
                .requestedAt(entity.getRequestedAt())
                .completedAt(entity.getCompletedAt())
                .failedAt(entity.getFailedAt())
                .cancelledAt(entity.getCancelledAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public PaymentOrderJpaEntity toEntity(PaymentOrder domain) {
        if (domain == null) return null;
        PaymentOrderJpaEntity entity = new PaymentOrderJpaEntity();
        entity.setId(domain.getId());
        entity.setPlanName(domain.getPlanName());
        entity.setPrice(domain.getPrice());
        entity.setDurationDays(domain.getDurationDays());
        entity.setGateway(domain.getGateway());
        entity.setStatus(domain.getStatus());
        entity.setTransactionCode(domain.getTransactionCode());
        entity.setGatewayTransactionId(domain.getGatewayTransactionId());
        entity.setCheckoutUrl(domain.getCheckoutUrl());
        entity.setFailureReason(domain.getFailureReason());
        entity.setMetadata(domain.getMetadata());
        entity.setRequestedAt(domain.getRequestedAt());
        entity.setCompletedAt(domain.getCompletedAt());
        entity.setFailedAt(domain.getFailedAt());
        entity.setCancelledAt(domain.getCancelledAt());
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
}


