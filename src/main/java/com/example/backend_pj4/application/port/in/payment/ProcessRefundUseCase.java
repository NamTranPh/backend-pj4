package com.example.backend_pj4.application.port.in.payment;

import com.example.backend_pj4.application.dto.payment.PaymentRefundResult;

public interface ProcessRefundUseCase {
    PaymentRefundResult execute(String refundId, String refundTransactionCode, String adminNote, String adminId);
}
