package com.example.backend_pj4.application.mapper;

import com.example.backend_pj4.application.dto.payment.PaymentRefundResult;
import com.example.backend_pj4.domain.model.PaymentRefund;

public class PaymentRefundResultMapper {

    private PaymentRefundResultMapper() {
    }

    public static PaymentRefundResult toResult(PaymentRefund refund) {
        if (refund == null) {
            return null;
        }

        return new PaymentRefundResult(
                refund.getId(),
                refund.getPaymentOrderId(),
                refund.getUserId(),
                refund.getAmount(),
                refund.getStatus() != null ? refund.getStatus().name() : null,
                refund.getReason(),
                refund.getAdminNote(),
                refund.getRefundTransactionCode(),
                refund.getProcessedBy(),
                refund.getRequestedAt(),
                refund.getProcessedAt(),
                refund.getCreatedAt()
        );
    }
}
