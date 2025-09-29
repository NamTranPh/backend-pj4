package com.example.backend_pj4.domain.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Episode {
    private String episodeId;
    private Movie movie;
    private Integer episodeNumber;
    private String title;
    private String description;
    private Integer duration;
    private String videoUrl;
    private String thumbnailUrl;
    private LocalDate airDate;
    private Boolean isPremium;
    private Integer viewCount;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private List<Comment> comments;
    private List<HistoryWatching> watchingHistory;
}