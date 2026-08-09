package com.example.backend_pj4.infrastructure.database.entities;

import com.example.backend_pj4.common.constants.enums.MembershipPaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "membership", indexes = {
        @Index(name = "idx_membership_user_id", columnList = "user_id"),
        @Index(name = "idx_membership_end_date", columnList = "end_date"),
        @Index(name = "idx_membership_deleted_at", columnList = "deleted_at")
})
@Where(clause = "deleted_at IS NULL")
@Getter
public class MembershipJpaEntity {

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

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private MembershipPaymentStatus paymentStatus;

    @Column(name = "auto_renewal")
    private Boolean autoRenewal;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public MembershipJpaEntity() {}

    public void setId(String id) { this.id = id; }

    public MembershipJpaEntity(String id, UserJpaEntity user, MembershipPlanJpaEntity plan,
                            LocalDate startDate, LocalDate endDate,
                            MembershipPaymentStatus paymentStatus, Boolean autoRenewal,
                            Boolean isActive, LocalDateTime deletedAt,
                            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.plan = plan;
        this.startDate = startDate;
        this.endDate = endDate;
        this.paymentStatus = paymentStatus;
        this.autoRenewal = autoRenewal;
        this.isActive = isActive;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void setUser(UserJpaEntity user) { this.user = user; }
    public void setPlan(MembershipPlanJpaEntity plan) { this.plan = plan; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void setPaymentStatus(MembershipPaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public void setAutoRenewal(Boolean autoRenewal) { this.autoRenewal = autoRenewal; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}

