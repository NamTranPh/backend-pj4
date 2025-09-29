package com.example.backend_pj4.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
public class ApiResponseDto<T> {

    @Schema(description = "Metadata about the response")
    private MetaDto meta;

    @Schema(description = "The data payload")
    private T data;

    @Schema(description = "Pagination information")
    private PaginationDto pagination;

    public static <T> ApiResponseDto<T> success(T data) {
        return ApiResponseDto.<T>builder()
                .meta(new MetaDto(true, "Success", null))
                .data(data)
                .build();
    }

    public static <T> ApiResponseDto<T> success(T data, PaginationDto pagination) {
        return ApiResponseDto.<T>builder()
                .meta(new MetaDto(true, "Success", null))
                .data(data)
                .pagination(pagination)
                .build();
    }

    public static <T> ApiResponseDto<T> fail(String message) {
        return ApiResponseDto.<T>builder()
                .meta(new MetaDto(false, message, null))
                .build();
    }
}