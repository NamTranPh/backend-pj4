package com.example.backend_pj4.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetaDto {

    @Schema(description = "Indicates the success or failure of the request", example = "true")
    private boolean status;

    @Schema(description = "A message describing the result", example = "Success")
    private String message;

    @Schema(description = "Additional metadata, if any", example = "null")
    private Object extra;
}