package com.example.backend_pj4.application.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.backend_pj4.domain.entities.User;
import com.example.backend_pj4.domain.enums.MembershipStatus;

@Data
public class UserResponse {
    private Integer userId;
    private String email;
    private String name;
    private LocalDate birthDate;
    private String profilePicture;
    private String phone;
    private String address;
    private String roleName;
    private MembershipStatus membershipStatus;
    private LocalDate membershipExpiryDate;
    private Boolean isActive;
    private LocalDateTime createdAt;
    
    public static UserResponse fromEntity(User user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setBirthDate(user.getBirthDate());
        response.setProfilePicture(user.getProfilePicture());
        response.setPhone(user.getPhone());
        response.setAddress(user.getAddress());
        response.setRoleName(user.getRole() != null ? user.getRole().getRoleName() : null);
        response.setMembershipStatus(user.getMembershipStatus());
        response.setMembershipExpiryDate(user.getMembershipExpiryDate());
        response.setIsActive(user.getIsActive());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}