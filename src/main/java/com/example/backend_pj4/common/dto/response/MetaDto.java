package com.example.backend_pj4.common.dto.response;

import com.example.backend_pj4.common.Iapi.response.IApiResponseMeta;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetaDto implements IApiResponseMeta {

    @Schema(description = "Indicates the success or failure of the request", example = "true")
    private boolean status;

    @Schema(description = "A message describing the result", example = "Success")
    private String message;

    @Schema(description = "Additional metadata, if any", nullable = true, example = "{}")
    private Object extra;

    public static MetaDto success(String message) {
        return new MetaDto(true, message, null);
    }

    public static MetaDto failure(String message) {
        return new MetaDto(false, message, null);
    }
}