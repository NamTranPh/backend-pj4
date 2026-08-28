package com.example.backend_pj4.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class MembershipPlan {
    private String id;
    private String name;
    private String slug;
    private BigDecimal price;
    private Integer durationDays;
    private Integer maxDevices;
    private Boolean canDownload;
    private String videoQuality;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
