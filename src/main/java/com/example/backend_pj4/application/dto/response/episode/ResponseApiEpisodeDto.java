package com.example.backend_pj4.application.dto.response.episode;

import com.example.backend_pj4.common.dto.response.ApiResponseDto;
import com.example.backend_pj4.common.dto.response.MetaDto;
import com.example.backend_pj4.common.dto.response.PaginationDto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ResponseApiEpisodeDto extends ApiResponseDto<EpisodeResponse> {

    // Constructor gọi super để khởi tạo meta, data, pagination
    public ResponseApiEpisodeDto(MetaDto meta, EpisodeResponse data, PaginationDto pagination) {
        super(meta, data, pagination);
    }

    public static ResponseApiEpisodeDto of(String message, EpisodeResponse data) {
        return new ResponseApiEpisodeDto(MetaDto.success(message), data, null);
    }
}
