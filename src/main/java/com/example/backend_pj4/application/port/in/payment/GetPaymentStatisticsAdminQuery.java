package com.example.backend_pj4.application.port.in.payment;

import java.time.LocalDate;

import com.example.backend_pj4.application.dto.payment.AdminPaymentStatisticsResult;

public interface GetPaymentStatisticsAdminQuery {
    AdminPaymentStatisticsResult execute(LocalDate fromDate, LocalDate toDate);
}
