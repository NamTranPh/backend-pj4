package com.example.backend_pj4.application.dto.response.user_cms;

import java.util.List;

import com.example.backend_pj4.common.dto.response.ApiResponseDto;
import com.example.backend_pj4.common.dto.response.MetaDto;
import com.example.backend_pj4.common.dto.response.PaginationDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response wrapper for an array of genres with pagination")
public class ResponseApiArrayUserDto extends ApiResponseDto<List<UserResponse>> {

    public ResponseApiArrayUserDto(MetaDto meta, List<UserResponse> data, PaginationDto pagination) {
        super(meta, data, pagination);
    }

    public static ResponseApiArrayUserDto of(String message, List<UserResponse> data, PaginationDto pagination) {
        return new ResponseApiArrayUserDto(MetaDto.success(message), data, pagination);
    }
}
