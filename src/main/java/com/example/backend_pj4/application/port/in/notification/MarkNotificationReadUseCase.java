package com.example.backend_pj4.application.port.in.notification;

public interface MarkNotificationReadUseCase {
    void execute(String notificationId, String userId);
}
