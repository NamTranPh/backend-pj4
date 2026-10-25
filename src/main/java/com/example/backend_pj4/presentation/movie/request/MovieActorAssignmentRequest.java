package com.example.backend_pj4.presentation.movie.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MovieActorAssignmentRequest(
        @NotBlank(message = "actorId không được để trống")
        String actorId,

        @Size(max = 255, message = "Tên vai diễn tối đa 255 ký tự")
        String characterName,

        Integer orderIndex
) {
}
