package com.example.backend_pj4.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Phone is required")
    // @Pattern(regexp = "^[0-9]{10,11}$", message = "Invalid phone number format")
    @Pattern(regexp = "^(admin|[0-9]{10,11})$", message = "Invalid phone number format")
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
