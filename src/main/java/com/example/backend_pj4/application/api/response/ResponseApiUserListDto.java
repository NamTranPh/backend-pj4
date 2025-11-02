package com.example.backend_pj4.application.api.response;

import com.example.backend_pj4.application.dto.response.user_cms.UserResponse;
import com.example.backend_pj4.common.dto.response.ApiResponseDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class ResponseApiUserListDto extends ApiResponseDto<List<UserResponse>> {
    @Schema(description = "Response data for list of users")
    private List<UserResponse> data;
}
