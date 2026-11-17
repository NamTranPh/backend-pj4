package com.example.backend_pj4.domain.repository.filter;

import java.time.LocalDate;

import com.example.backend_pj4.common.constants.enums.PaymentOrderStatus;

public record PaymentOrderFilter(
        PaymentOrderStatus status,
        String gateway,
        String membershipPlanId,
        String userId,
        LocalDate fromDate,
        LocalDate toDate,
        String search
) {
}
