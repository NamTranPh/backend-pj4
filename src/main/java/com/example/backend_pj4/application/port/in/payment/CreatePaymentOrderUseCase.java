package com.example.backend_pj4.application.port.in.payment;

import com.example.backend_pj4.application.dto.payment.PaymentOrderResult;

public interface CreatePaymentOrderUseCase {
    PaymentOrderResult execute(String userId, String planSlug);
}
