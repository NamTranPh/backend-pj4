package com.example.backend_pj4.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class Actor {
    private String id;
    private String name;
    private String originalName;
    private String avatarUrl;
    private String bio;
    private LocalDate birthDate;
    private String nationality;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
