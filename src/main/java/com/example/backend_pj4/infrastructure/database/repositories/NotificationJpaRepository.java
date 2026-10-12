package com.example.backend_pj4.infrastructure.database.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend_pj4.infrastructure.database.entities.NotificationJpaEntity;

public interface NotificationJpaRepository extends JpaRepository<NotificationJpaEntity, String> {

    /** Tìm theo tiêu đề/nội dung, kèm lọc đã đọc. Tham số null thì bỏ qua điều kiện đó. */
    @Query("SELECT n FROM NotificationJpaEntity n WHERE n.user.id = :userId "
            + "AND (:search IS NULL OR :search = '' "
            + "     OR LOWER(n.title) LIKE LOWER(CONCAT('%', :search, '%')) "
            + "     OR LOWER(n.content) LIKE LOWER(CONCAT('%', :search, '%'))) "
            + "AND (:isRead IS NULL OR n.isRead = :isRead)")
    Page<NotificationJpaEntity> search(@Param("userId") String userId,
                                       @Param("search") String search,
                                       @Param("isRead") Boolean isRead,
                                       Pageable pageable);

    long countByUser_IdAndIsReadFalse(String userId);

    @Modifying
    @Query("UPDATE NotificationJpaEntity n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.id = :id")
    void markAsRead(String id);
}
