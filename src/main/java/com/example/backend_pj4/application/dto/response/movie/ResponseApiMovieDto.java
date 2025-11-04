package com.example.backend_pj4.application.dto.response.movie;

import com.example.backend_pj4.common.dto.response.ApiResponseDto;
import com.example.backend_pj4.common.dto.response.MetaDto;
import com.example.backend_pj4.common.dto.response.PaginationDto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ResponseApiMovieDto extends ApiResponseDto<MovieResponse> {

    // Constructor gọi super để khởi tạo meta, data, pagination
    public ResponseApiMovieDto(MetaDto meta, MovieResponse data, PaginationDto pagination) {
        super(meta, data, pagination);
    }

    public static ResponseApiMovieDto of(String message, MovieResponse data) {
        return new ResponseApiMovieDto(MetaDto.success(message), data, null);
    }
}
