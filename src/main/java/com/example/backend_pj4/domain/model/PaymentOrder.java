package com.example.backend_pj4.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.backend_pj4.common.constants.enums.PaymentOrderStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class PaymentOrder {
    private String id;
    private String userId;
    private String membershipPlanId;
    private String planName;
    private BigDecimal price;
    private Integer durationDays;
    private String gateway;
    private PaymentOrderStatus status;
    private String transactionCode;
    private String gatewayTransactionId;
    private String checkoutUrl;
    private String failureReason;
    private String metadata;
    private LocalDateTime requestedAt;
    private LocalDateTime completedAt;
    private LocalDateTime failedAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
