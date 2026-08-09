package com.example.backend_pj4.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class HistoryWatching {
    private String id;
    private String userId;
    private String movieId;
    private String episodeId;
    private Integer watchDuration;
    private Integer totalDuration;
    private BigDecimal progress;
    private Boolean isCompleted;
    private Integer lastPosition;
    private LocalDateTime watchedAt;
    private LocalDateTime createdAt;
}
