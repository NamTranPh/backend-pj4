package com.example.backend_pj4.infrastructure.database.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.backend_pj4.infrastructure.database.entities.OutboxEventJpaEntity;

public interface SpringDataOutboxEventRepository extends JpaRepository<OutboxEventJpaEntity, String> {
    List<OutboxEventJpaEntity> findByProcessedFalseOrderByCreatedAtAsc();

    @Modifying
    @Query("UPDATE OutboxEventJpaEntity e SET e.processed = true, e.processedAt = CURRENT_TIMESTAMP WHERE e.id = :id")
    void markAsProcessed(String id);

    @Modifying
    @Query("DELETE FROM OutboxEventJpaEntity e WHERE e.processed = true AND e.createdAt < :threshold")
    void deleteProcessedBefore(LocalDateTime threshold);
}


