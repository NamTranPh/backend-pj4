package com.example.backend_pj4.domain.repository.filter;

import java.time.LocalDate;

import com.example.backend_pj4.common.constants.enums.RefundStatus;

public record PaymentRefundFilter(
        RefundStatus status,
        String paymentOrderId,
        String userId,
        LocalDate fromDate,
        LocalDate toDate,
        String search
) {
}
