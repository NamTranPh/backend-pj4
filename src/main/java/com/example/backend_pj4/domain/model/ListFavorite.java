package com.example.backend_pj4.domain.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class ListFavorite {
    private String id;
    private String userId;
    private String movieId;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
}
