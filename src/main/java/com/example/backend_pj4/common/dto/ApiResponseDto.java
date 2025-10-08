package com.example.backend_pj4.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDto<T> {

    @Schema(description = "Metadata about the response")
    private MetaDto meta;

    @Schema(
        description = "The data payload, which can be an object, array, or specific DTO type"
    )
    private T data;

    @Schema(description = "Pagination information")
    private PaginationDto pagination;
}
