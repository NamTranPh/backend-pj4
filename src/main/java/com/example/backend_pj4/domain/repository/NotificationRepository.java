package com.example.backend_pj4.domain.repository;

import java.util.Optional;

import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.Notification;
import com.example.backend_pj4.domain.repository.filter.NotificationFilter;

public interface NotificationRepository {
    Notification save(Notification notification);
    Optional<Notification> findById(String id);
    PageResult<Notification> find(NotificationFilter filter, PageQuery query);
    long countUnreadByUserId(String userId);
    void markAsRead(String id);
    void deleteById(String id);
}
