package com.example.backend_pj4.application.dto.response.user_cms;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.backend_pj4.application.dto.response.role.RoleResponse;
import com.example.backend_pj4.domain.entities.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCmsResponse {

    @Schema(description = "Unique identifier of the user")
    private String userId;

    @Schema(description = "User phone number")
    private String phone;

    @Schema(description = "User email address")
    private String email;

    @Schema(description = "Full name of the user")
    private String name;

    @Schema(description = "Birth date of the user")
    private LocalDate birthDate;

    @Schema(description = "Profile picture URL")
    private String profilePicture;

    @Schema(description = "Residential address")
    private String address;

    @Schema(description = "Role of the user")
    private RoleResponse role;

    // @Schema(description = "Membership status of the user")
    // private MembershipStatus membershipStatus;

    // @Schema(description = "Membership expiry date")
    // private LocalDate membershipExpiryDate;

    @Schema(description = "Is the user active?")
    private Boolean isActive;

    @Schema(description = "Account creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Account last update timestamp")
    private LocalDateTime updatedAt;

    public static UserCmsResponse fromDomain(User user) {
        return UserCmsResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .birthDate(user.getBirthDate())
                .profilePicture(user.getProfilePicture())
                .address(user.getAddress())
                .role(RoleResponse.fromDomain(user.getRole()))
                // .membershipStatus(user.getMembershipStatus())
                // .membershipExpiryDate(user.getMembershipExpiryDate())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

    }
}
