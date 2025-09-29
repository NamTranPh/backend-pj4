package com.example.backend_pj4.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RequestPaginationDto {

    @Schema(description = "The current page number", example = "1")
    private int page = 1;

    @Schema(description = "The number of items per page", example = "10")
    private int limit = 10;
}