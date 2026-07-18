//Entity – Domain thuần túy
package com.example.backend_pj4.domain.entities;

import java.time.LocalDateTime;

import com.example.backend_pj4.common.enums.RoleEnum;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Role {
    private String roleId;
    private RoleEnum roleName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}