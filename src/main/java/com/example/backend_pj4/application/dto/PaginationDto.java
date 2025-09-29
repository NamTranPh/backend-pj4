package com.example.backend_pj4.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationDto {

    @Schema(description = "The current page number", example = "1")
    private int page;

    @Schema(description = "The number of items per page", example = "10")
    private int limit;

    @Schema(description = "The total number of items", example = "0")
    private long totalItems;

    @Schema(description = "The total number of pages", example = "0")
    private long totalPages;
}
