package com.example.backend_pj4.infrastructure.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_event", indexes = {
        @Index(name = "idx_outbox_event_aggregate_id", columnList = "aggregate_id"),
        @Index(name = "idx_outbox_event_event_type", columnList = "event_type"),
        @Index(name = "idx_outbox_event_processed", columnList = "processed"),
        @Index(name = "idx_outbox_event_created_at", columnList = "created_at")
})
@Getter
public class OutboxEventJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "aggregate_id", nullable = false, length = 36)
    private String aggregateId;

    @Column(name = "aggregate_type", nullable = false, length = 50)
    private String aggregateType;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "event_data", columnDefinition = "JSON", nullable = false)
    private String eventData;

    @Column(name = "processed")
    private Boolean processed;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public OutboxEventJpaEntity() {}

    public OutboxEventJpaEntity(String id, String aggregateId, String aggregateType, String eventType,
                             String eventData, Boolean processed, LocalDateTime processedAt,
                             LocalDateTime createdAt) {
        this.id = id;
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.eventType = eventType;
        this.eventData = eventData;
        this.processed = processed;
        this.processedAt = processedAt;
        this.createdAt = createdAt;
    }

    public void setAggregateId(String aggregateId) { this.aggregateId = aggregateId; }
    public void setAggregateType(String aggregateType) { this.aggregateType = aggregateType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public void setEventData(String eventData) { this.eventData = eventData; }
    public void setProcessed(Boolean processed) { this.processed = processed; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
    public void setId(String id) { this.id = id; }
}

