package com.example.backend_pj4.application.usecase.notification;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.notification.NotificationResult;
import com.example.backend_pj4.application.mapper.NotificationResultMapper;
import com.example.backend_pj4.application.port.in.notification.ListUserNotificationsUseCase;
import com.example.backend_pj4.domain.repository.NotificationRepository;

@Service
public class ListUserNotificationsService implements ListUserNotificationsUseCase {

    private final NotificationRepository notificationRepository;

    public ListUserNotificationsService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResult> execute(String userId) {
        return notificationRepository.findByUserId(userId).stream()
                .map(NotificationResultMapper::toResult)
                .toList();
    }
}
