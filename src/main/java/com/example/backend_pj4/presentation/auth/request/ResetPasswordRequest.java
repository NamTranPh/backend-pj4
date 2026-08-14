package com.example.backend_pj4.presentation.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank @Email String email,
        @NotBlank String otpCode,
        @NotBlank @Size(min = 6, max = 72) String newPassword
) {}
