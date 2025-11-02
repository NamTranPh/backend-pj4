package com.example.backend_pj4.application.dto.response.role;

import java.time.LocalDateTime;

import com.example.backend_pj4.domain.entities.Role;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleResponse {
    private String roleId;
    private String roleName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static RoleResponse fromDomain(Role role) {
        if (role == null) {
            return null;
        }
        return RoleResponse.builder()
                .roleId(role.getRoleId())
                .roleName(role.getRoleName())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }
}
