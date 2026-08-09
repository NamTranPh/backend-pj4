package com.example.backend_pj4.domain.model;

import java.time.LocalDateTime;

import com.example.backend_pj4.common.constants.enums.NotificationReferenceType;
import com.example.backend_pj4.common.constants.enums.NotificationType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class Notification {
    private String id;
    private String userId;
    private NotificationType type;
    private String title;
    private String content;
    private NotificationReferenceType referenceType;
    private String referenceId;
    private Boolean isRead;
    private LocalDateTime readAt;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
}
