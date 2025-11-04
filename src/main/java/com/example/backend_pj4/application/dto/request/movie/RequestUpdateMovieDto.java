package com.example.backend_pj4.application.dto.request.movie;

import java.util.List;

import com.example.backend_pj4.common.enums.MovieStatus;
import com.example.backend_pj4.common.enums.MovieType;

import lombok.Data;

@Data
public class RequestUpdateMovieDto {
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
    private MovieStatus status;
    private Boolean isPremium;
    private Boolean isFeatured;
    private Boolean isActive;

    private List<String> genreIds;
}
