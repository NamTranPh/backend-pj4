package com.example.backend_pj4.presentation.payment.request;

import jakarta.validation.constraints.NotBlank;

public record AdminCancelPaymentRequest(
        @NotBlank(message = "Lý do hủy đơn không được để trống")
        String reason
) {
}
