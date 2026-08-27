package com.example.backend_pj4.presentation.membership.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateMembershipPlanRequest(
        @Size(max = 100)
        String name,

        @Positive
        BigDecimal price,

        @Positive
        Integer durationDays,

        @Positive
        Integer maxDevices,

        Boolean canDownload,

        @Size(max = 20)
        String videoQuality,

        String description,

        Boolean isActive
) {
}
