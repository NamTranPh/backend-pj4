package com.example.backend_pj4.application.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentRefundResult(
        String id,
        String paymentOrderId,
        String userId,
        BigDecimal amount,
        String status,
        String reason,
        String adminNote,
        String refundTransactionCode,
        String processedBy,
        LocalDateTime requestedAt,
        LocalDateTime processedAt,
        LocalDateTime createdAt
) {
}
