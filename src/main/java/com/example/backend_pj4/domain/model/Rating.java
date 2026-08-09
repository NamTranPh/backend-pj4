package com.example.backend_pj4.domain.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class Rating {
    private String id;
    private String userId;
    private String movieId;
    private Integer score;
    private String review;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
