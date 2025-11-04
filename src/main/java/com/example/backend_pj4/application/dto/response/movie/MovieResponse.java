package com.example.backend_pj4.application.dto.response.movie;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.backend_pj4.application.dto.response.episode.EpisodeResponse;
import com.example.backend_pj4.common.enums.MovieStatus;
import com.example.backend_pj4.common.enums.MovieType;
import com.example.backend_pj4.domain.entities.Movie;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieResponse {

    @Schema(description = "Unique identifier of the movie")
    private String movieId;

    @Schema(description = "Title of the movie")
    private String title;

    @Schema(description = "Original title of the movie")
    private String originalTitle;

    @Schema(description = "Movie description")
    private String description;

    @Schema(description = "Release year")
    private Integer releaseYear;

    @Schema(description = "Duration in minutes")
    private Integer duration;

    @Schema(description = "Director name")
    private String director;

    @Schema(description = "Actors in the movie")
    private String actors;

    @Schema(description = "Country of origin")
    private String country;

    @Schema(description = "Language")
    private String language;

    @Schema(description = "Trailer URL")
    private String trailerUrl;

    @Schema(description = "Poster URL")
    private String posterUrl;

    @Schema(description = "Backdrop URL")
    private String backdropUrl;

    @Schema(description = "Rating of the movie")
    private BigDecimal rating;

    @Schema(description = "View count")
    private Integer viewCount;

    @Schema(description = "Type of the movie")
    private MovieType movieType;

    @Schema(description = "Total number of episodes")
    private Integer totalEpisodes;

    @Schema(description = "Movie status")
    private MovieStatus status;

    @Schema(description = "Is premium content?")
    private Boolean isPremium;

    @Schema(description = "Is featured?")
    private Boolean isFeatured;

    @Schema(description = "Is active?")
    private Boolean isActive;

    @Schema(description = "Creator of the movie")
    private String createdBy;

    @Schema(description = "Genres associated with the movie")
    private List<String> genres;

    @Schema(description = "Episodes of the movie")
    private List<EpisodeResponse> episodes;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    public static MovieResponse fromDomain(Movie movie) {
        return MovieResponse.builder()
                .movieId(movie.getMovieId())
                .title(movie.getTitle())
                .originalTitle(movie.getOriginalTitle())
                .description(movie.getDescription())
                .releaseYear(movie.getReleaseYear())
                .duration(movie.getDuration())
                .director(movie.getDirector())
                .actors(movie.getActors())
                .country(movie.getCountry())
                .language(movie.getLanguage())
                .trailerUrl(movie.getTrailerUrl())
                .posterUrl(movie.getPosterUrl())
                .backdropUrl(movie.getBackdropUrl())
                .rating(movie.getRating())
                .viewCount(movie.getViewCount())
                .movieType(movie.getMovieType())
                .totalEpisodes(movie.getTotalEpisodes())
                .status(movie.getStatus())
                .isPremium(movie.getIsPremium())
                .isFeatured(movie.getIsFeatured())
                .isActive(movie.getIsActive())
                .createdBy(movie.getCreatedBy() != null ? movie.getCreatedBy().getUserId() : null)
                .genres(movie.getGenres() != null ? movie.getGenres().stream().map(g -> g.getName()).toList() : List.of())
                .episodes(movie.getEpisodes() != null ? movie.getEpisodes().stream().map(EpisodeResponse::fromDomain).toList() : List.of())
                .createdAt(movie.getCreatedAt())
                .updatedAt(movie.getUpdatedAt())
                .build();
    }
}
