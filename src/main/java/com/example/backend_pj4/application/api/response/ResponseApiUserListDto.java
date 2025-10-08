package com.example.backend_pj4.application.api.response;

import com.example.backend_pj4.application.dto.response.UserResponse;
import com.example.backend_pj4.common.dto.ApiResponseDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class ResponseApiUserListDto extends ApiResponseDto<List<UserResponse>> {
    @Schema(description = "Response data for list of users")
    private List<UserResponse> data;
}
