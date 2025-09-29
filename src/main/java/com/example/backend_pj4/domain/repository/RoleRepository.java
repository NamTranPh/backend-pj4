package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.domain.entities.Role;

public interface RoleRepository {
    // Basic CRUD
    Role save(Role role);
    Optional<Role> findById(String roleId);
    List<Role> findAll();
    void deleteById(String roleId);

    // Business queries
    Optional<Role> findByRoleName(String roleName);
    boolean existsByRoleName(String roleName);
}