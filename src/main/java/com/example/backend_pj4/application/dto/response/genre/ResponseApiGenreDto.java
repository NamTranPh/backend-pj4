package com.example.backend_pj4.application.dto.response.genre;

import com.example.backend_pj4.common.dto.response.ApiResponseDto;
import com.example.backend_pj4.common.dto.response.MetaDto;
import com.example.backend_pj4.common.dto.response.PaginationDto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ResponseApiGenreDto extends ApiResponseDto<GenreResponse> {

    // Constructor gọi super để khởi tạo meta, data, pagination
    public ResponseApiGenreDto(MetaDto meta, GenreResponse data, PaginationDto pagination) {
        super(meta, data, pagination);
    }

    public static ResponseApiGenreDto of(String message, GenreResponse data) {
        return new ResponseApiGenreDto(MetaDto.success(message), data, null);
    }
}
