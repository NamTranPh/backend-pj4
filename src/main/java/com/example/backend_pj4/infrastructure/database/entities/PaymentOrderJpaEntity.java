package com.example.backend_pj4.infrastructure.database.entities;

import com.example.backend_pj4.common.constants.enums.PaymentOrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_order", indexes = {
        @Index(name = "idx_payment_order_user_id", columnList = "user_id"),
        @Index(name = "idx_payment_order_status", columnList = "status"),
        @Index(name = "idx_payment_order_transaction_code", columnList = "transaction_code")
})
@Getter
public class PaymentOrderJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membership_plan_id", nullable = false)
    private MembershipPlanJpaEntity plan;

    @Column(name = "plan_name", nullable = false, length = 100)
    private String planName;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @Column(name = "gateway", nullable = false, length = 50)
    private String gateway;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentOrderStatus status;

    @Column(name = "transaction_code", unique = true, nullable = false, length = 100)
    private String transactionCode;

    @Column(name = "gateway_transaction_id", length = 255)
    private String gatewayTransactionId;

    @Column(name = "checkout_url", length = 500)
    private String checkoutUrl;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "failed_at")
    private LocalDateTime failedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public PaymentOrderJpaEntity() {}

    public PaymentOrderJpaEntity(String id, UserJpaEntity user, MembershipPlanJpaEntity plan,
                              String planName, BigDecimal price, Integer durationDays,
                              String gateway, PaymentOrderStatus status, String transactionCode,
                              String gatewayTransactionId, String checkoutUrl, String failureReason,
                              String metadata, LocalDateTime requestedAt, LocalDateTime completedAt,
                              LocalDateTime failedAt, LocalDateTime cancelledAt,
                              LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.plan = plan;
        this.planName = planName;
        this.price = price;
        this.durationDays = durationDays;
        this.gateway = gateway;
        this.status = status;
        this.transactionCode = transactionCode;
        this.gatewayTransactionId = gatewayTransactionId;
        this.checkoutUrl = checkoutUrl;
        this.failureReason = failureReason;
        this.metadata = metadata;
        this.requestedAt = requestedAt;
        this.completedAt = completedAt;
        this.failedAt = failedAt;
        this.cancelledAt = cancelledAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void setUser(UserJpaEntity user) { this.user = user; }
    public void setPlan(MembershipPlanJpaEntity plan) { this.plan = plan; }
    public void setPlanName(String planName) { this.planName = planName; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }
    public void setGateway(String gateway) { this.gateway = gateway; }
    public void setStatus(PaymentOrderStatus status) { this.status = status; }
    public void setTransactionCode(String transactionCode) { this.transactionCode = transactionCode; }
    public void setGatewayTransactionId(String gatewayTransactionId) { this.gatewayTransactionId = gatewayTransactionId; }
    public void setCheckoutUrl(String checkoutUrl) { this.checkoutUrl = checkoutUrl; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public void setFailedAt(LocalDateTime failedAt) { this.failedAt = failedAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }
    public void setId(String id) { this.id = id; }
}

