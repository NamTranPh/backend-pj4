package com.example.backend_pj4.presentation.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResendRegistrationOtpRequest(
        @NotBlank @Email String email
) {}
