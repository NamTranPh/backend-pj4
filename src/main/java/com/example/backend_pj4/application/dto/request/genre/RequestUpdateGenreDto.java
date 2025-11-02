package com.example.backend_pj4.application.dto.request.genre;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequestUpdateGenreDto {
    @Schema(description = "Name of the genre", example = "Action")
    @NotBlank(message = "Genre name must not be blank")
    private String name;

    @Schema(description = "Description of the genre", example = "High-intensity action movies")
    private String description;
}
