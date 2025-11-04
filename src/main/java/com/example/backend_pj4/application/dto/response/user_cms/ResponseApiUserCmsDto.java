package com.example.backend_pj4.application.dto.response.user_cms;

import com.example.backend_pj4.common.dto.response.ApiResponseDto;
import com.example.backend_pj4.common.dto.response.MetaDto;
import com.example.backend_pj4.common.dto.response.PaginationDto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ResponseApiUserCmsDto extends ApiResponseDto<UserCmsResponse> {

    // Constructor gọi super để khởi tạo meta, data, pagination
    public ResponseApiUserCmsDto(MetaDto meta, UserCmsResponse data, PaginationDto pagination) {
        super(meta, data, pagination);
    }

    public static ResponseApiUserCmsDto of(String message, UserCmsResponse data) {
        return new ResponseApiUserCmsDto(MetaDto.success(message), data, null);
    }
}
