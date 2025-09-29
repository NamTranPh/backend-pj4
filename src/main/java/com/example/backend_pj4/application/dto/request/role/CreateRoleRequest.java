package com.example.backend_pj4.application.dto.request.role;

import com.example.backend_pj4.domain.entities.Role;

import lombok.Data;

@Data
public class CreateRoleRequest {
    private String name;

    public Role toDomain() {
        return Role.builder()
                .roleName(this.name)
                .build();
    }
}