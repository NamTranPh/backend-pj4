package com.example.backend_pj4.application.dto.response.genre;

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
public class ResponseApiArrayGenreDto extends ApiResponseDto<List<GenreResponse>> {

    public ResponseApiArrayGenreDto(MetaDto meta, List<GenreResponse> data, PaginationDto pagination) {
        super(meta, data, pagination);
    }

    public static ResponseApiArrayGenreDto of(String message, List<GenreResponse> data, PaginationDto pagination) {
        return new ResponseApiArrayGenreDto(MetaDto.success(message), data, pagination);
    }
}
