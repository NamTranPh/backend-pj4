package com.example.backend_pj4.application.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequestChangePasswordDto {

    @Schema(description = "Old password of the user", example = "oldpassword")
    @NotBlank(message = "Old password is required")
    private String oldPassword;

    @Schema(description = "New password of the user", example = "newpassword")
    @NotBlank(message = "New password is required")
    private String newPassword;
}