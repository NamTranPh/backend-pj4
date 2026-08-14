package com.example.backend_pj4.presentation.user.request;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(max = 100) String name,
        @Size(max = 20) String phone,
        @Size(max = 500) String address,
        @Size(max = 500) String profileUrl
) {}
