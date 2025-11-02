package com.example.backend_pj4.application.dto.response.genre;

import java.time.LocalDateTime;

import com.example.backend_pj4.domain.entities.Genre;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenreResponse {
    @Schema(description = "Unique identifier of the genre")
    private String genreId;

    @Schema(description = "Name of the genre")
    private String name;

    @Schema(description = "Description of the genre")
    private String description;

    @Schema(description = "Account creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Account last update timestamp")
    private LocalDateTime updatedAt;

    public static GenreResponse fromDomain(Genre genre) {
        return GenreResponse.builder()
                .genreId(genre.getGenreId())
                .name(genre.getName())
                .description(genre.getDescription())
                .createdAt(genre.getCreatedAt())
                .updatedAt(genre.getUpdatedAt())
                .build();
    }
}
