package com.example.backend_pj4.infrastructure.databases.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend_pj4.common.enums.RoleEnum;
import com.example.backend_pj4.infrastructure.databases.entities.RoleEntity;

@Repository
public interface JpaRoleRepository extends JpaRepository<RoleEntity, String> {
    Optional<RoleEntity> findByRoleName(RoleEnum roleName);
    boolean existsByRoleName(RoleEnum roleName);
}