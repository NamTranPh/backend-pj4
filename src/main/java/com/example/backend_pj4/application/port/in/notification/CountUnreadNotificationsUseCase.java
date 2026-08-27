package com.example.backend_pj4.application.port.in.notification;

public interface CountUnreadNotificationsUseCase {
    long execute(String userId);
}
