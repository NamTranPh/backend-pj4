package com.example.backend_pj4.application.dto.response.episode;

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
public class ResponseApiArrayEpisodeDto extends ApiResponseDto<List<EpisodeResponse>> {
    public ResponseApiArrayEpisodeDto(MetaDto meta, List<EpisodeResponse> data, PaginationDto pagination) {
        super(meta, data, pagination);
    }

    public static ResponseApiArrayEpisodeDto of(String message, List<EpisodeResponse> data, PaginationDto pagination) {
        return new ResponseApiArrayEpisodeDto(MetaDto.success(message), data, pagination);
    }
}
