package com.example.backend_pj4.infrastructure.database.mappers;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.model.OutboxEvent;
import com.example.backend_pj4.infrastructure.database.entities.OutboxEventJpaEntity;

@Component
public class OutboxEventPersistenceMapper {

    public OutboxEvent toDomain(OutboxEventJpaEntity entity) {
        if (entity == null) return null;
        return OutboxEvent.builder()
                .id(entity.getId())
                .aggregateId(entity.getAggregateId())
                .aggregateType(entity.getAggregateType())
                .eventType(entity.getEventType())
                .eventData(entity.getEventData())
                .processed(entity.getProcessed())
                .processedAt(entity.getProcessedAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public OutboxEventJpaEntity toEntity(OutboxEvent domain) {
        if (domain == null) return null;
        OutboxEventJpaEntity entity = new OutboxEventJpaEntity();
        entity.setId(domain.getId());
        entity.setAggregateId(domain.getAggregateId());
        entity.setAggregateType(domain.getAggregateType());
        entity.setEventType(domain.getEventType());
        entity.setEventData(domain.getEventData());
        entity.setProcessed(domain.getProcessed());
        entity.setProcessedAt(domain.getProcessedAt());
        return entity;
    }
}


