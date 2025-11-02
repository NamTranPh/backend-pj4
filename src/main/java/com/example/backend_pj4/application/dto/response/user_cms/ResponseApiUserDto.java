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
public class ResponseApiUserDto extends ApiResponseDto<UserResponse> {

    // Constructor gọi super để khởi tạo meta, data, pagination
    public ResponseApiUserDto(MetaDto meta, UserResponse data, PaginationDto pagination) {
        super(meta, data, pagination);
    }

    public static ResponseApiUserDto of(String message, UserResponse data) {
        return new ResponseApiUserDto(MetaDto.success(message), data, null);
    }
}
