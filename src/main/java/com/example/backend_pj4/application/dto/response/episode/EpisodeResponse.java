package com.example.backend_pj4.application.dto.response.episode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.backend_pj4.domain.entities.Episode;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EpisodeResponse {

    @Schema(description = "Unique identifier of the episode")
    private String episodeId;

    @Schema(description = "Episode number")
    private Integer episodeNumber;

    @Schema(description = "Episode title")
    private String title;

    @Schema(description = "Episode description")
    private String description;

    @Schema(description = "Duration in minutes")
    private Integer duration;

    @Schema(description = "Video URL")
    private String videoUrl;

    @Schema(description = "Thumbnail URL")
    private String thumbnailUrl;

    @Schema(description = "Air date of the episode")
    private LocalDate airDate;

    @Schema(description = "Is premium content?")
    private Boolean isPremium;

    @Schema(description = "View count")
    private Integer viewCount;

    @Schema(description = "Is active?")
    private Boolean isActive;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Comments of the episode")
    private List<String> comments;

    public static EpisodeResponse fromDomain(Episode episode) {
        return EpisodeResponse.builder()
                .episodeId(episode.getEpisodeId())
                .episodeNumber(episode.getEpisodeNumber())
                .title(episode.getTitle())
                .description(episode.getDescription())
                .duration(episode.getDuration())
                .videoUrl(episode.getVideoUrl())
                .thumbnailUrl(episode.getThumbnailUrl())
                .airDate(episode.getAirDate())
                .isPremium(episode.getIsPremium())
                .viewCount(episode.getViewCount())
                .isActive(episode.getIsActive())
                .createdAt(episode.getCreatedAt())
                .comments(episode.getComments() != null ? episode.getComments().stream().map(c -> c.getContent()).toList() : List.of())
                .build();
    }
}
