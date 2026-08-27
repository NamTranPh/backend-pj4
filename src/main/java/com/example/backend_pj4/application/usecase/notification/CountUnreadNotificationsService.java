package com.example.backend_pj4.application.usecase.notification;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.port.in.notification.CountUnreadNotificationsUseCase;
import com.example.backend_pj4.domain.repository.NotificationRepository;

@Service
public class CountUnreadNotificationsService implements CountUnreadNotificationsUseCase {

    private final NotificationRepository notificationRepository;

    public CountUnreadNotificationsService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public long execute(String userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }
}
