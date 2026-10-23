package com.example.backend_pj4.application.dto.membership;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MyMembershipResult(
        String id,
        String planId,
        String planName,
        String planSlug,
        BigDecimal price,
        Integer maxDevices,
        Boolean canDownload,
        String videoQuality,
        LocalDate startDate,
        LocalDate endDate,
        long daysRemaining,
        Boolean autoRenewal,
        Boolean isActive
) {
}
