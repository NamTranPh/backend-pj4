package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.common.enums.RoleEnum;
import com.example.backend_pj4.domain.entities.Role;

public interface RoleRepository {
    Role save(Role role);
    Optional<Role> findById(String roleId);
    List<Role> findAll();
    void deleteById(String roleId);
    Optional<Role> findByRoleName(RoleEnum roleName);
    boolean existsByRoleName(RoleEnum roleName);
}