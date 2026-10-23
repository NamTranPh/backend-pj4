package com.example.backend_pj4.application.mapper;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.application.dto.user.UserProfileResult;
import com.example.backend_pj4.application.service.StorageUrlResolver;
import com.example.backend_pj4.domain.model.User;

@Component
public class UserResultMapper {

    private final StorageUrlResolver storageUrlResolver;

    public UserResultMapper(StorageUrlResolver storageUrlResolver) {
        this.storageUrlResolver = storageUrlResolver;
    }

    public UserProfileResult toResult(User user) {
        return toResult(user, false, null, null);
    }

    public UserProfileResult toResult(User user, boolean isVip, String currentPlanName, LocalDate membershipEndDate) {
        return new UserProfileResult(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhone(),
                user.getAddress(),
                storageUrlResolver.resolveAvatar(user.getProfileUrl()),
                user.getRole() != null ? user.getRole().name() : null,
                user.getAccountStatus() != null ? user.getAccountStatus().name() : null,
                Boolean.TRUE.equals(user.getIsBanned()),
                Boolean.TRUE.equals(user.getEmailVerified()),
                user.getCreatedAt(),
                isVip,
                currentPlanName,
                membershipEndDate
        );
    }
}
