package com.example.backend_pj4.infrastructure.database.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.backend_pj4.common.constants.enums.RefundStatus;

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
import lombok.Getter;

@Entity 
@Table(name = "payment_refund", indexes = {
        @Index(name = "idx_payment_refund_payment_order_id", columnList = "payment_order_id"),
        @Index(name = "idx_payment_refund_user_id", columnList = "user_id"),
        @Index(name = "idx_payment_refund_status", columnList = "status")
})
@Getter
public class PaymentRefundJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_order_id", nullable = false)
    private PaymentOrderJpaEntity paymentOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private RefundStatus status;

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "admin_note", columnDefinition = "TEXT")
    private String adminNote;

    @Column(name = "refund_transaction_code", length = 100)
    private String refundTransactionCode;

    @Column(name = "processed_by", length = 36)
    private String processedBy;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public PaymentRefundJpaEntity() {}

    public PaymentRefundJpaEntity(String id, PaymentOrderJpaEntity paymentOrder, UserJpaEntity user,
                                  BigDecimal amount, RefundStatus status, String reason, String adminNote,
                                  String refundTransactionCode, String processedBy, LocalDateTime requestedAt,
                                  LocalDateTime processedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.paymentOrder = paymentOrder;
        this.user = user;
        this.amount = amount;
        this.status = status;
        this.reason = reason;
        this.adminNote = adminNote;
        this.refundTransactionCode = refundTransactionCode;
        this.processedBy = processedBy;
        this.requestedAt = requestedAt;
        this.processedAt = processedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void setId(String id) { this.id = id; }
    public void setPaymentOrder(PaymentOrderJpaEntity paymentOrder) { this.paymentOrder = paymentOrder; }
    public void setUser(UserJpaEntity user) { this.user = user; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setStatus(RefundStatus status) { this.status = status; }
    public void setReason(String reason) { this.reason = reason; }
    public void setAdminNote(String adminNote) { this.adminNote = adminNote; }
    public void setRefundTransactionCode(String refundTransactionCode) { this.refundTransactionCode = refundTransactionCode; }
    public void setProcessedBy(String processedBy) { this.processedBy = processedBy; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
}
