package com.example.backend_pj4.application.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AdminPaymentDetailResult(
        String id,
        UserInfo user,
        PlanInfo plan,
        BigDecimal price,
        Integer durationDays,
        String gateway,
        String status,
        String transactionCode,
        String gatewayTransactionId,
        String checkoutUrl,
        String failureReason,
        String metadata,
        LocalDateTime requestedAt,
        LocalDateTime completedAt,
        LocalDateTime failedAt,
        LocalDateTime cancelledAt,
        LocalDateTime refundedAt,
        BigDecimal refundedAmount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<PaymentRefundResult> refundHistory
) {
    public record UserInfo(String id, String email, String name, String profileUrl) {}
    public record PlanInfo(String id, String name, Integer durationDays, BigDecimal price) {}
}
