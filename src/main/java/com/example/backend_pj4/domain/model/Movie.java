package com.example.backend_pj4.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.backend_pj4.common.constants.enums.MovieType;
import com.example.backend_pj4.common.constants.enums.VideoStatus;
import com.example.backend_pj4.common.constants.enums.VideoVisibility;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class Movie {
    private String id;
    private String title;
    private String originalTitle;
    private String description;
    private Integer releaseYear;
    private Integer duration;
    private String director;
    private String actors;
    private String country;
    private String language;
    private String trailerUrl;
    private String posterUrl;
    private String backdropUrl;
    private MovieType movieType;
    private Integer totalEpisodes;
    private VideoStatus status;
    private VideoVisibility visibility;
    private Boolean isPremium;
    private Boolean isFeatured;
    private BigDecimal rating;
    private Long viewCount;
    private String rawFileKey;
    private String masterPlaylistKey;
    private String resolutions;
    private User createdBy;
    private List<Genre> genres;
    private List<Episode> episodes;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
