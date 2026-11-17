package com.example.backend_pj4.application.port.in.payment;

import java.math.BigDecimal;

import com.example.backend_pj4.application.dto.payment.PaymentRefundResult;

public interface CreateRefundUseCase {
    PaymentRefundResult execute(String paymentOrderId, BigDecimal amount, String reason, String adminId);
}
