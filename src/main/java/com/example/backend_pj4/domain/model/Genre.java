package com.example.backend_pj4.domain.model;

import java.time.LocalDateTime;

import com.example.backend_pj4.common.constants.enums.GenreStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class Genre {
    private String id;
    private String name;
    private String slug;
    private String icon;
    private String description;
    private GenreStatus status;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
