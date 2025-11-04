package com.example.backend_pj4.application.dto.request.episode;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestCreateEpisodeDto {
    @NotNull
    private Integer episodeNumber;
    @NotNull
    private String title;
    private String description;
    private Integer duration;
    private String videoUrl;
    private String thumbnailUrl;
    private LocalDate airDate;
    private Boolean isPremium;
    private Boolean isActive;
}
