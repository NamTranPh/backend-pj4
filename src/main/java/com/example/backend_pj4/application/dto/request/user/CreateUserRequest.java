package com.example.backend_pj4.application.dto.request.user;

import java.time.LocalDate;

import com.example.backend_pj4.domain.entities.User;
import com.example.backend_pj4.domain.enums.MembershipStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Invalid phone number format")
    private String phone;

    private LocalDate birthDate;

    private String profilePicture;

    private String address;

    public User toDomain() {
        return User.builder()
                .email(this.email)
                .password(this.password)
                .name(this.name)
                .phone(this.phone)
                .birthDate(this.birthDate)
                .profilePicture(this.profilePicture)
                .address(this.address)
                .membershipStatus(MembershipStatus.FREE)
                .isActive(true)
                .build();
    }
}