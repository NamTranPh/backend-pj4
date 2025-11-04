package com.example.backend_pj4.application.dto.request.user_cms;

import com.example.backend_pj4.common.dto.request.RequestPaginationDto;
import com.example.backend_pj4.common.enums.SortOrder;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RequestGetUserCmsDto extends RequestPaginationDto {
    @Schema(description = "Keyword for searching by genre name")
    private String q;

    @Schema(description = "Field name to sort by (e.g., name, createdAt)")
    private String sortBy;

    private SortOrder sortOrder;
}
