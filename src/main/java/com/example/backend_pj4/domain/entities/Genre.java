package com.example.backend_pj4.domain.entities;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Genre {
    private String genreId;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}