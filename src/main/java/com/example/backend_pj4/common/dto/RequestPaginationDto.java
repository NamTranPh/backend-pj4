package com.example.backend_pj4.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RequestPaginationDto {
    @Schema(
        description = "The current page number",
        example = "1",
        type = "integer"
    )
    private Integer page;

    @Schema(
        description = "The number of items per page",
        example = "10",
        type = "integer"
    )
    private Integer limit;
}