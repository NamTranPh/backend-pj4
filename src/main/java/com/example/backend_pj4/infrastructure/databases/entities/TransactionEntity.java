package com.example.backend_pj4.infrastructure.databases.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.example.backend_pj4.domain.enums.PaymentMethod;
import com.example.backend_pj4.domain.enums.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transaction",
    indexes = {
        @Index(name = "idx_user_transaction", columnList = "user_id"),
        @Index(name = "idx_transaction_date", columnList = "transaction_date"),
        @Index(name = "idx_payment_status", columnList = "payment_status")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membership_id")
    private MembershipEntity membership;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "transaction_code", unique = true, length = 100)
    private String transactionCode;

    @Column(name = "gateway_transaction_id")
    private String gatewayTransactionId;

    @CreationTimestamp
    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}