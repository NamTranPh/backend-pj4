package com.example.backend_pj4.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.backend_pj4.common.constants.enums.VideoStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class Episode {
    private String id;
    private String movieId;
    private Integer episodeNumber;
    private String title;
    private String description;
    private Integer duration;
    private VideoStatus status;
    private String rawFileKey;
    private String masterPlaylistKey;
    private String resolutions;
    private String thumbnailUrl;
    private LocalDate airDate;
    private Boolean isPremium;
    private Long viewCount;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
}
