package com.example.backend_pj4.common.dto.response;

import com.example.backend_pj4.common.Iapi.response.IApiResponsePagination;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginationDto implements IApiResponsePagination {

    @Schema(description = "The current page number", example = "1")
    private int page;

    @Schema(description = "The number of items per page", example = "10")
    private int limit;

    @Schema(description = "The total number of items", example = "0")
    private long totalItems;

    @Schema(description = "The total number of pages", example = "0")
    private int totalPages;

    public static PaginationDto of(int page, int limit, long totalItems, int totalPages) {
        return new PaginationDto(page, limit, totalItems, totalPages);
    }

}
