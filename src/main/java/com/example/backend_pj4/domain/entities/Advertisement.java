package com.example.backend_pj4.domain.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.backend_pj4.domain.enums.AdType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Advertisement {
    private String adId;
    private String title;
    private String content;
    private String imageUrl;
    private String clickUrl;
    private AdType adType;
    private String position;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer clickCount;
    private Integer viewCount;
    private Boolean isActive;
    private User createdBy;
    private LocalDateTime createdAt;
}