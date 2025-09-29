package com.example.backend_pj4.domain.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class HistoryWatching {
    private String historyId;
    private User user;
    private Movie movie;
    private Episode episode;
    private Integer watchDuration;
    private Integer totalDuration;
    private BigDecimal progress;
    private Boolean isCompleted;
    private LocalDateTime watchedAt;
    private Integer lastPosition;
}