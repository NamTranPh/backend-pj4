package com.example.backend_pj4.application.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentOrderResult(
        String id,
        String planName,
        BigDecimal price,
        String status,
        String transactionCode,
        String checkoutUrl,
        LocalDateTime createdAt,
        LocalDateTime completedAt
) {
}
