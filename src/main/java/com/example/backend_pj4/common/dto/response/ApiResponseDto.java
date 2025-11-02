package com.example.backend_pj4.common.dto.response;

import com.example.backend_pj4.common.Iapi.response.IApiResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDto<T> implements IApiResponse<T> {

    @Schema(description = "Metadata about the response")
    private MetaDto meta;

    @Schema(description = "The data payload, which can be an object, array, or specific DTO type")
    private T data;

    @Schema(description = "Pagination information")
    private PaginationDto pagination;

    // -------------------------------
    // ✅ Static factory methods
    // -------------------------------

    /** Success without pagination */
    public static <T> ApiResponseDto<T> success(T data) {
        return new ApiResponseDto<>(MetaDto.success("Success"), data, null);
    }

    /** Success with custom message */
    public static <T> ApiResponseDto<T> success(T data, String message) {
        return new ApiResponseDto<>(MetaDto.success(message), data, null);
    }

    /** ✅ Success with pagination */
    public static <T> ApiResponseDto<T> success(T data, PaginationDto pagination) {
        return new ApiResponseDto<>(MetaDto.success("Success"), data, pagination);
    }

    /** Error response */
    public static <T> ApiResponseDto<T> error(String message) {
        return new ApiResponseDto<>(MetaDto.failure(message), null, null);
    }
}
