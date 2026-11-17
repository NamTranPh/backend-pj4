package com.example.backend_pj4.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.backend_pj4.common.constants.enums.RefundStatus;

import lombok.Builder;
import lombok.Getter;

@Getter 
@Builder(toBuilder = true)
public class PaymentRefund {
    private String id;
    private String paymentOrderId;
    private String userId;
    private BigDecimal amount;
    private RefundStatus status;
    private String reason;
    private String adminNote;
    private String refundTransactionCode;
    private String processedBy;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
