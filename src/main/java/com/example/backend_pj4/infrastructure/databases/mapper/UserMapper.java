package com.example.backend_pj4.infrastructure.databases.mapper;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.entities.User;
import com.example.backend_pj4.infrastructure.databases.entities.UserEntity;

@Component
public class UserMapper {
    private final RoleMapper roleMapper;

    public UserMapper(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    public User toDomain(UserEntity entity) {
        if (entity == null)
            return null;
        return User.builder()
                .userId(entity.getUserId())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .password(entity.getPassword())
                .name(entity.getName())
                .birthDate(entity.getBirthDate())
                .profilePicture(entity.getProfilePicture())
                .address(entity.getAddress())
                .role(roleMapper.toDomain(entity.getRole())) // convert RoleEntity -> Role
                .membershipStatus(entity.getMembershipStatus())
                .membershipExpiryDate(entity.getMembershipExpiryDate())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public UserEntity toEntity(User user) {
        if (user == null)
            return null;
        UserEntity entity = new UserEntity();
        entity.setUserId(user.getUserId());
        entity.setEmail(user.getEmail());
        entity.setPhone(user.getPhone());
        entity.setPassword(user.getPassword());
        entity.setName(user.getName());
        entity.setBirthDate(user.getBirthDate());
        entity.setProfilePicture(user.getProfilePicture());
        entity.setAddress(user.getAddress());
        entity.setRole(roleMapper.toEntity(user.getRole())); // convert Role -> RoleEntity
        entity.setMembershipStatus(user.getMembershipStatus());
        entity.setMembershipExpiryDate(user.getMembershipExpiryDate());
        entity.setIsActive(user.getIsActive());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());
        return entity;
    }
}
