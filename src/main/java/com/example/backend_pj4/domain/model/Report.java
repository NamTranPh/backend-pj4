package com.example.backend_pj4.domain.model;

import java.time.LocalDateTime;

import com.example.backend_pj4.common.constants.enums.ReportReason;
import com.example.backend_pj4.common.constants.enums.ReportStatus;
import com.example.backend_pj4.common.constants.enums.ReportTargetType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class Report {
    private String id;
    private String reporterId;
    private ReportTargetType targetType;
    private String targetId;
    private ReportReason reason;
    private String description;
    private ReportStatus status;
    private String resolvedBy;
    private String adminNote;
    private LocalDateTime resolvedAt;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
}
