package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.domain.model.Notification;

public interface NotificationRepository {
    Notification save(Notification notification);
    Optional<Notification> findById(String id);
    List<Notification> findAll();
    void deleteById(String id);
    List<Notification> findByUserId(String userId);
    List<Notification> findUnreadByUserId(String userId);
    long countUnreadByUserId(String userId);
    void markAsRead(String id);
}
