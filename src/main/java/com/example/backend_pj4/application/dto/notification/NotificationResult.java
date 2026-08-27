package com.example.backend_pj4.application.dto.notification;

import java.time.LocalDateTime;

import com.example.backend_pj4.common.constants.enums.NotificationReferenceType;
import com.example.backend_pj4.common.constants.enums.NotificationType;

public record NotificationResult(
        String id,
        NotificationType type,
        String title,
        String content,
        NotificationReferenceType referenceType,
        String referenceId,
        Boolean isRead,
        LocalDateTime readAt,
        LocalDateTime createdAt
) {
}
