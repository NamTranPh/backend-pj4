package com.example.backend_pj4.application.dto.response.movie;

import java.util.List;

import com.example.backend_pj4.common.dto.response.ApiResponseDto;
import com.example.backend_pj4.common.dto.response.MetaDto;
import com.example.backend_pj4.common.dto.response.PaginationDto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ResponseApiArrayMovieDto extends ApiResponseDto<List<MovieResponse>> {

    public ResponseApiArrayMovieDto(MetaDto meta, List<MovieResponse> data, PaginationDto pagination) {
        super(meta, data, pagination);
    }

    public static ResponseApiArrayMovieDto of(String message, List<MovieResponse> data, PaginationDto pagination) {
        return new ResponseApiArrayMovieDto(MetaDto.success(message), data, pagination);
    }
}
