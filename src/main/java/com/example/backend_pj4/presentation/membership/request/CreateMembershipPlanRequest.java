package com.example.backend_pj4.presentation.membership.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateMembershipPlanRequest(
        @NotBlank @Size(max = 100)
        String name,

        @NotNull @Positive
        BigDecimal price,

        @NotNull @Positive
        Integer durationDays,

        @Positive
        Integer maxDevices,

        Boolean canDownload,

        @Size(max = 20)
        String videoQuality,

        String description
) {
}
