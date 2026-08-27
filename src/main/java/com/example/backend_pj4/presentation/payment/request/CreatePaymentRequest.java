package com.example.backend_pj4.presentation.payment.request;

import jakarta.validation.constraints.NotBlank;

public record CreatePaymentRequest(
        @NotBlank String planSlug
) {
}
