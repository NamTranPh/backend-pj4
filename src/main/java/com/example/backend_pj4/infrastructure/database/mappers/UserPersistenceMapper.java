package com.example.backend_pj4.infrastructure.database.mappers;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.infrastructure.database.entities.UserJpaEntity;

@Component
public class UserPersistenceMapper {

    public User toDomain(UserJpaEntity entity) {
        if (entity == null) return null;
        return User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .name(entity.getName())
                .profileUrl(entity.getProfileUrl())
                .phone(entity.getPhone())
                .address(entity.getAddress())
                .role(entity.getRole())
                .emailVerified(entity.getEmailVerified())
                .accountStatus(entity.getAccountStatus())
                .isBanned(entity.getIsBanned())
                .failedLoginAttempts(entity.getFailedLoginAttempts())
                .firstFailureAt(entity.getFirstFailureAt())
                .lockedUntil(entity.getLockedUntil())
                .lastActiveAt(entity.getLastActiveAt())
                .deletedAt(entity.getDeletedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public UserJpaEntity toEntity(User domain) {
        if (domain == null) return null;
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(domain.getId());
        entity.setEmail(domain.getEmail());
        entity.setPassword(domain.getPassword());
        entity.setName(domain.getName());
        entity.setProfileUrl(domain.getProfileUrl());
        entity.setPhone(domain.getPhone());
        entity.setAddress(domain.getAddress());
        entity.setRole(domain.getRole());
        entity.setEmailVerified(domain.getEmailVerified());
        entity.setAccountStatus(domain.getAccountStatus());
        entity.setIsBanned(domain.getIsBanned());
        entity.setFailedLoginAttempts(domain.getFailedLoginAttempts());
        entity.setFirstFailureAt(domain.getFirstFailureAt());
        entity.setLockedUntil(domain.getLockedUntil());
        entity.setLastActiveAt(domain.getLastActiveAt());
        entity.setDeletedAt(domain.getDeletedAt());
        return entity;
    }

    public User toSimpleDomain(UserJpaEntity entity) {
        if (entity == null) return null;
        return User.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .profileUrl(entity.getProfileUrl())
                .build();
    }
}


