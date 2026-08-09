package com.example.backend_pj4.common.response;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDto<T> {

    @Schema(description = "The status of the response: success or error", example = "success")
    private String status;

    @Schema(description = "Machine-readable programmatic code", example = "success")
    private String code;

    @Schema(description = "Friendly human-readable message", example = "Successfully")
    private String message;

    @Schema(description = "The data payload")
    private T data;

    @Schema(description = "Pagination information if the response contains paginated list data")
    private PaginationDto pagination;

    @Schema(description = "Details about request validation errors if any")
    private Object errors;

    @Schema(description = "The timestamp of response creation")
    @Builder.Default
    private Instant timestamp = Instant.now();

    // ==========================================
    // ✅ Static Factory Methods for Quick Builds
    // ==========================================

    /** General Success with default message and no pagination */
    public static <T> ApiResponseDto<T> success(T data) {
        return ApiResponseDto.<T>builder()
                .status("success")
                .code("success")
                .message("Successfully")
                .data(data)
                .build();
    }

    /** Success with custom message and no pagination */
    public static <T> ApiResponseDto<T> success(T data, String message) {
        return ApiResponseDto.<T>builder()
                .status("success")
                .code("success")
                .message(message)
                .data(data)
                .build();
    }

    /** Success with pagination and default message */
    public static <T> ApiResponseDto<T> success(T data, PaginationDto pagination) {
        return ApiResponseDto.<T>builder()
                .status("success")
                .code("success")
                .message("Successfully")
                .data(data)
                .pagination(pagination)
                .build();
    }

    /** Success with pagination and custom message */
    public static <T> ApiResponseDto<T> success(T data, PaginationDto pagination, String message) {
        return ApiResponseDto.<T>builder()
                .status("success")
                .code("success")
                .message(message)
                .data(data)
                .pagination(pagination)
                .build();
    }

    /** Standard Error response */
    public static <T> ApiResponseDto<T> error(String code, String message) {
        return ApiResponseDto.<T>builder()
                .status("error")
                .code(code)
                .message(message)
                .build();
    }

    /** Error response with validation errors or additional details */
    public static <T> ApiResponseDto<T> error(String code, String message, Object errors) {
        return ApiResponseDto.<T>builder()
                .status("error")
                .code(code)
                .message(message)
                .errors(errors)
                .build();
    }
}
