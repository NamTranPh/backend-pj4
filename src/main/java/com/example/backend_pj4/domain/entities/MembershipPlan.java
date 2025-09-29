package com.example.backend_pj4.domain.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class MembershipPlan {
    private String planId;
    private String planName;
    private BigDecimal price;
    private Integer durationDays;
    private Integer maxDevices;
    private Boolean canDownload;
    private String videoQuality; // xem xet co the co enum
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private List<Membership> memberships;
}