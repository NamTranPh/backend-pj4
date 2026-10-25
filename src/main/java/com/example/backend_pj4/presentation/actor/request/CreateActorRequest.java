package com.example.backend_pj4.presentation.actor.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateActorRequest(
        @NotBlank(message = "Tên diễn viên không được để trống")
        @Size(max = 255, message = "Tên diễn viên tối đa 255 ký tự")
        String name,

        @Size(max = 255, message = "Tên gốc tối đa 255 ký tự")
        String originalName,

        String avatarUrl,

        String bio,

        LocalDate birthDate,

        @Size(max = 100, message = "Quốc tịch tối đa 100 ký tự")
        String nationality
) {
}
