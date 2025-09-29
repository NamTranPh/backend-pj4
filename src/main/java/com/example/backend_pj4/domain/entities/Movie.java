package com.example.backend_pj4.domain.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Movie {
    private String movieId;
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
    private BigDecimal rating;
    private Integer viewCount;
    private String movieType;
    private Integer totalEpisodes;
    private String status;
    private Boolean isPremium;
    private Boolean isFeatured;
    private Boolean isActive;
    private User createdBy;
    private List<Genre> genres;
    private List<Episode> episodes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}