package com.example.backend_pj4.application.mapper;

import com.example.backend_pj4.application.dto.payment.PaymentOrderResult;
import com.example.backend_pj4.domain.model.PaymentOrder;

public class PaymentResultMapper {

    private PaymentResultMapper() {
    }

    public static PaymentOrderResult toResult(PaymentOrder order) {
        if (order == null) {
            return null;
        }

        return new PaymentOrderResult(
                order.getId(),
                order.getPlanName(),
                order.getPrice(),
                order.getStatus() != null ? order.getStatus().name() : null,
                order.getTransactionCode(),
                order.getCheckoutUrl(),
                order.getGateway(),
                order.getDurationDays(),
                order.getFailureReason(),
                order.getRequestedAt(),
                order.getCompletedAt(),
                order.getFailedAt(),
                order.getCancelledAt(),
                order.getRefundedAt(),
                order.getRefundedAmount(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
