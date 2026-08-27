package com.example.backend_pj4.application.port.in.notification;

import java.util.List;

import com.example.backend_pj4.application.dto.notification.NotificationResult;

public interface ListUserNotificationsUseCase {
    List<NotificationResult> execute(String userId);
}
