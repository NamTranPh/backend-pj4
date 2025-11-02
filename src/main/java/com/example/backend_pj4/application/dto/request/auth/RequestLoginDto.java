package com.example.backend_pj4.application.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequestLoginDto {

    @NotBlank(message = "Phone is required")
    // @Pattern(regexp = "^(admin|[0-9]{10,11})$", message = "Invalid phone number format")
    private String phone;

    @NotBlank(message = "Password is required")
    // @Size(min = 3, message = "Password must be at least 3 characters")
    private String password;
}
