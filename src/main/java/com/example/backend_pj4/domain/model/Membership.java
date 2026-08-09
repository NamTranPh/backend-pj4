package com.example.backend_pj4.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.backend_pj4.common.constants.enums.MembershipPaymentStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class Membership {
    private String id;
    private String userId;
    private String membershipPlanId;
    private LocalDate startDate;
    private LocalDate endDate;
    private MembershipPaymentStatus paymentStatus;
    private Boolean autoRenewal;
    private Boolean isActive;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
