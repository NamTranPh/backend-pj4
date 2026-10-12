package com.example.backend_pj4.application.query.notification;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.notification.NotificationResult;
import com.example.backend_pj4.application.mapper.NotificationResultMapper;
import com.example.backend_pj4.application.port.in.notification.ListUserNotificationsQuery;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.repository.NotificationRepository;
import com.example.backend_pj4.domain.repository.filter.NotificationFilter;

@Service
public class ListUserNotificationsQueryHandler implements ListUserNotificationsQuery {

    private final NotificationRepository notificationRepository;

    public ListUserNotificationsQueryHandler(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<NotificationResult> execute(NotificationFilter filter, PageQuery query) {
        return notificationRepository.find(filter, query).map(NotificationResultMapper::toResult);
    }
}
