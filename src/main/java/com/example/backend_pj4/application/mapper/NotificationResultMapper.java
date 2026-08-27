package com.example.backend_pj4.application.mapper;

import com.example.backend_pj4.application.dto.notification.NotificationResult;
import com.example.backend_pj4.domain.model.Notification;

public class NotificationResultMapper {

    private NotificationResultMapper() {
    }

    public static NotificationResult toResult(Notification n) {
        return new NotificationResult(
                n.getId(),
                n.getType(),
                n.getTitle(),
                n.getContent(),
                n.getReferenceType(),
                n.getReferenceId(),
                n.getIsRead(),
                n.getReadAt(),
                n.getCreatedAt()
        );
    }
}
