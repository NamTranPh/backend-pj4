package com.example.backend_pj4.infrastructure.database.mappers;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.model.PaymentRefund;
import com.example.backend_pj4.infrastructure.database.entities.PaymentOrderJpaEntity;
import com.example.backend_pj4.infrastructure.database.entities.PaymentRefundJpaEntity;
import com.example.backend_pj4.infrastructure.database.entities.UserJpaEntity;

@Component
public class PaymentRefundPersistenceMapper {

    public PaymentRefund toDomain(PaymentRefundJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return PaymentRefund.builder()
                .id(entity.getId())
                .paymentOrderId(entity.getPaymentOrder() != null ? entity.getPaymentOrder().getId() : null)
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .amount(entity.getAmount())
                .status(entity.getStatus())
                .reason(entity.getReason())
                .adminNote(entity.getAdminNote())
                .refundTransactionCode(entity.getRefundTransactionCode())
                .processedBy(entity.getProcessedBy())
                .requestedAt(entity.getRequestedAt())
                .processedAt(entity.getProcessedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public PaymentRefundJpaEntity toEntity(PaymentRefund domain) {
        if (domain == null) {
            return null;
        }

        PaymentRefundJpaEntity entity = new PaymentRefundJpaEntity();
        entity.setId(domain.getId());
        entity.setAmount(domain.getAmount());
        entity.setStatus(domain.getStatus());
        entity.setReason(domain.getReason());
        entity.setAdminNote(domain.getAdminNote());
        entity.setRefundTransactionCode(domain.getRefundTransactionCode());
        entity.setProcessedBy(domain.getProcessedBy());
        entity.setRequestedAt(domain.getRequestedAt());
        entity.setProcessedAt(domain.getProcessedAt());

        if (domain.getPaymentOrderId() != null) {
            PaymentOrderJpaEntity orderEntity = new PaymentOrderJpaEntity();
            orderEntity.setId(domain.getPaymentOrderId());
            entity.setPaymentOrder(orderEntity);
        }

        if (domain.getUserId() != null) {
            UserJpaEntity userEntity = new UserJpaEntity();
            userEntity.setId(domain.getUserId());
            entity.setUser(userEntity);
        }

        return entity;
    }
}
