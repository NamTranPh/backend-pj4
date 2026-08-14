package com.example.backend_pj4.application.mapper;

import com.example.backend_pj4.application.dto.user.UserProfileResult;
import com.example.backend_pj4.domain.model.User;

public final class UserResultMapper {

    private UserResultMapper() {}

    public static UserProfileResult toResult(User user) {
        return new UserProfileResult(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhone(),
                user.getAddress(),
                user.getProfileUrl(),
                user.getRole() != null ? user.getRole().name() : null,
                user.getAccountStatus() != null ? user.getAccountStatus().name() : null,
                Boolean.TRUE.equals(user.getEmailVerified()),
                user.getCreatedAt()
        );
    }
}
