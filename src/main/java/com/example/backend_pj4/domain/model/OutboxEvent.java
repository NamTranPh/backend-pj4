package com.example.backend_pj4.domain.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class OutboxEvent {
    private String id;
    private String aggregateId;
    private String aggregateType;
    private String eventType;
    private String eventData;
    private Boolean processed;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;
}
