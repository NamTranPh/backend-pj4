package com.example.backend_pj4.domain.entities;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Rating {
    private Integer ratingId;
    private User user;
    private Movie movie;
    private Integer score;
    private String review;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}