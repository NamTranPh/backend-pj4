package com.example.backend_pj4.domain.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.backend_pj4.common.enums.PaymentMethod;
import com.example.backend_pj4.common.enums.PaymentStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Transaction {
    private String transactionId;
    private User user;
    private Membership membership;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String transactionCode;
    private String gatewayTransactionId;
    private LocalDateTime transactionDate;
    private LocalDateTime completedAt;
    private LocalDateTime refundedAt;
    private String notes;
}