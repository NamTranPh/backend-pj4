package com.example.backend_pj4.application.command.membership;

import java.math.BigDecimal;

public record CreateMembershipPlanCommand(
        String name,
        BigDecimal price,
        Integer durationDays,
        Integer maxDevices,
        Boolean canDownload,
        String videoQuality,
        String description
) {
}
