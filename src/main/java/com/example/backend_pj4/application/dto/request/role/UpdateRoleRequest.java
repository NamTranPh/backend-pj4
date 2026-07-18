package com.example.backend_pj4.application.dto.request.role;

import com.example.backend_pj4.common.enums.RoleEnum;
import com.example.backend_pj4.domain.entities.Role;

import lombok.Data;

@Data
public class UpdateRoleRequest {
    private RoleEnum roleName;

    public Role toDomain() {
        return Role.builder()
                .roleName(this.roleName)
                .build();
    }
}