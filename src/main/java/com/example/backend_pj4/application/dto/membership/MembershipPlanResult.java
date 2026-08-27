package com.example.backend_pj4.application.dto.membership;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MembershipPlanResult(
        String id,
        String name,
        String slug,
        BigDecimal price,
        Integer durationDays,
        Integer maxDevices,
        Boolean canDownload,
        String videoQuality,
        String description,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
