package com.example.backend_pj4.domain.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.backend_pj4.common.enums.MembershipStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class User {
    private String userId;
    private String email;
    private String phone;
    private String password;
    private String name;
    private LocalDate birthDate;
    private String profilePicture;
    private String address;
    private Role role;
    private MembershipStatus membershipStatus;
    private LocalDate membershipExpiryDate;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}