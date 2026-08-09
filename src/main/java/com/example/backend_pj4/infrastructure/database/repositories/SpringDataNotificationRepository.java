package com.example.backend_pj4.infrastructure.database.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.backend_pj4.infrastructure.database.entities.NotificationJpaEntity;

public interface SpringDataNotificationRepository extends JpaRepository<NotificationJpaEntity, String> {
    List<NotificationJpaEntity> findByUser_IdOrderByCreatedAtDesc(String userId);
    List<NotificationJpaEntity> findByUser_IdAndIsReadFalseOrderByCreatedAtDesc(String userId);
    long countByUser_IdAndIsReadFalse(String userId);

    @Modifying
    @Query("UPDATE NotificationJpaEntity n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.id = :id")
    void markAsRead(String id);
}


