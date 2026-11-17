package com.example.backend_pj4.application.port.in.payment;

import com.example.backend_pj4.application.dto.payment.AdminPaymentDetailResult;

public interface GetPaymentDetailAdminQuery {
    AdminPaymentDetailResult execute(String orderId);
}
