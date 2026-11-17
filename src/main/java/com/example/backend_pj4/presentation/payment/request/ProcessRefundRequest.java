package com.example.backend_pj4.presentation.payment.request;

import jakarta.validation.constraints.NotBlank;

public record ProcessRefundRequest(
        @NotBlank(message = "Mã giao dịch hoàn tiền không được để trống")
        String refundTransactionCode,

        String adminNote
) {
}
