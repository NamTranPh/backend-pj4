package com.example.backend_pj4.presentation.payment.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRefundRequest(
        @NotBlank(message = "Mã đơn hàng không được để trống")
        String paymentOrderId,

        @NotNull(message = "Số tiền hoàn không được để trống")
        @DecimalMin(value = "1000.00", message = "Số tiền hoàn tối thiểu là 1,000 đ")
        BigDecimal amount,

        @NotBlank(message = "Lý do hoàn tiền không được để trống")
        String reason
) {
}
