package com.example.backend_pj4.domain.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.backend_pj4.common.enums.PaymentStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Membership {
    private String memberId;
    private User user;
    private MembershipPlan plan;
    private LocalDate startDate;
    private LocalDate endDate;
    private PaymentStatus paymentStatus;
    private Boolean autoRenewal;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private List<Transaction> transactions;
}