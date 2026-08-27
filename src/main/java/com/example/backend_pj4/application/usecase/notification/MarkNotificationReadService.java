package com.example.backend_pj4.application.usecase.notification;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.port.in.notification.MarkNotificationReadUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Notification;
import com.example.backend_pj4.domain.repository.NotificationRepository;

@Service
public class MarkNotificationReadService implements MarkNotificationReadUseCase {

    private final NotificationRepository notificationRepository;

    public MarkNotificationReadService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public void execute(String notificationId, String userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        if (!notification.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        notificationRepository.markAsRead(notificationId);
    }
}
