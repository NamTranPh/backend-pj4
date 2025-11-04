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
public class ResponseApiArrayUserCmsDto extends ApiResponseDto<List<UserCmsResponse>> {

    public ResponseApiArrayUserCmsDto(MetaDto meta, List<UserCmsResponse> data, PaginationDto pagination) {
        super(meta, data, pagination);
    }

    public static ResponseApiArrayUserCmsDto of(String message, List<UserCmsResponse> data, PaginationDto pagination) {
        return new ResponseApiArrayUserCmsDto(MetaDto.success(message), data, pagination);
    }
}
