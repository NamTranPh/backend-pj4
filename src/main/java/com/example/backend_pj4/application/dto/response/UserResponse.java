// package com.example.backend_pj4.application.dto.response;

// import java.time.LocalDate;
// import java.time.LocalDateTime;

// import com.example.backend_pj4.domain.entities.User;
// import com.example.backend_pj4.domain.enums.MembershipStatus;

// import lombok.Data;

// @Data
// public class UserResponse {
//     private String userId;
//     private String phone;
//     private String email;
//     private String name;
//     private LocalDate birthDate;
//     private String profilePicture;
//     private String address;
//     private String roleName;
//     private MembershipStatus membershipStatus;
//     private LocalDate membershipExpiryDate;
//     private Boolean isActive;
//     private LocalDateTime createdAt;

//     public static UserResponse fromDomain(User user) {
//         UserResponse response = new UserResponse();
//         response.setUserId(user.getUserId());
//         response.setEmail(user.getEmail());
//         response.setName(user.getName());
//         response.setPhone(user.getPhone());
//         response.setBirthDate(user.getBirthDate());
//         response.setProfilePicture(user.getProfilePicture());
//         response.setAddress(user.getAddress());
//         response.setRoleName(user.getRole() != null ? user.getRole().getRoleName() : null);
//         response.setMembershipStatus(user.getMembershipStatus());
//         response.setMembershipExpiryDate(user.getMembershipExpiryDate());
//         response.setIsActive(user.getIsActive());
//         response.setCreatedAt(user.getCreatedAt());
//         return response;
//     }
// }


package com.example.backend_pj4.application.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.backend_pj4.domain.entities.User;
import com.example.backend_pj4.domain.enums.MembershipStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserResponse {

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
    private String roleName;

    @Schema(description = "Membership status of the user")
    private MembershipStatus membershipStatus;

    @Schema(description = "Membership expiry date")
    private LocalDate membershipExpiryDate;

    @Schema(description = "Is the user active?")
    private Boolean isActive;

    @Schema(description = "Account creation timestamp")
    private LocalDateTime createdAt;

    public static UserResponse fromDomain(User user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setPhone(user.getPhone());
        response.setBirthDate(user.getBirthDate());
        response.setProfilePicture(user.getProfilePicture());
        response.setAddress(user.getAddress());
        response.setRoleName(user.getRole() != null ? user.getRole().getRoleName() : null);
        response.setMembershipStatus(user.getMembershipStatus());
        response.setMembershipExpiryDate(user.getMembershipExpiryDate());
        response.setIsActive(user.getIsActive());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}
