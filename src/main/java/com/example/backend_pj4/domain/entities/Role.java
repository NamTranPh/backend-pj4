//Entity – Domain thuần túy
package com.example.backend_pj4.domain.entities;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Role {
    private String roleId;
    private String roleName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}