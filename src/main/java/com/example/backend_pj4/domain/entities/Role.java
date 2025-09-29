//Entity – Domain thuần túy
package com.example.backend_pj4.domain.entities;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class Role {
    private String roleId;
    private String roleName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}