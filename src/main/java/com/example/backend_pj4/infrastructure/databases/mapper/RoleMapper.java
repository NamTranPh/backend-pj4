package com.example.backend_pj4.infrastructure.databases.mapper;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.entities.Role;
import com.example.backend_pj4.infrastructure.databases.entities.RoleEntity;

@Component
public class RoleMapper {
    public Role toDomain(RoleEntity entity) {
        if (entity == null)
            return null;
        return Role.builder()
                .roleId(entity.getRoleId())
                .roleName(entity.getRoleName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public RoleEntity toEntity(Role role) {
        if (role == null)
            return null;
        RoleEntity entity = new RoleEntity();
        entity.setRoleId(role.getRoleId());
        entity.setRoleName(role.getRoleName());
        entity.setCreatedAt(role.getCreatedAt());
        entity.setUpdatedAt(role.getUpdatedAt());
        return entity;
    }
}
