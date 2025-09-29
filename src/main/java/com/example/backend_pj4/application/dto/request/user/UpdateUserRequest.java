package com.example.backend_pj4.application.dto.request.user;

import java.time.LocalDate;

import com.example.backend_pj4.domain.entities.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @Email(message = "Invalid email format")
    private String email;

    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Pattern(regexp = "^[0-9]{10,11}$", message = "Invalid phone number format")
    private String phone;

    private LocalDate birthDate;

    private String profilePicture;

    private String address;

    public User toDomain() {
        return User.builder()
                .email(this.email)
                .name(this.name)
                .phone(this.phone)
                .birthDate(this.birthDate)
                .profilePicture(this.profilePicture)
                .address(this.address)
                .build();
    }
}
