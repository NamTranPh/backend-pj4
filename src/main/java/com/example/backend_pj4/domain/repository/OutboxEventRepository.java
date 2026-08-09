package com.example.backend_pj4.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.domain.model.OutboxEvent;

public interface OutboxEventRepository {
    OutboxEvent save(OutboxEvent event);
    Optional<OutboxEvent> findById(String id);
    List<OutboxEvent> findUnprocessed();
    void markAsProcessed(String id);
    void deleteProcessedBefore(LocalDateTime threshold);
}
