package com.example.backend_pj4.application.port.in.payment;

import com.example.backend_pj4.application.dto.payment.PaymentRefundResult;

public interface CancelRefundUseCase {
    PaymentRefundResult execute(String refundId, String adminNote, String adminId);
}
