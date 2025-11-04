package com.example.backend_pj4.infrastructure.databases.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.common.enums.RoleEnum;
import com.example.backend_pj4.domain.entities.Role;
import com.example.backend_pj4.domain.repository.RoleRepository;
import com.example.backend_pj4.infrastructure.databases.mapper.RoleMapper;
import com.example.backend_pj4.infrastructure.databases.repository.JpaRoleRepository;

@Repository 
public class RoleRepositoryImpl implements RoleRepository {
    private final JpaRoleRepository jpaRoleRepository;
    private final RoleMapper roleMapper;

    public RoleRepositoryImpl(JpaRoleRepository jpaRoleRepository, RoleMapper roleMapper) {
        this.jpaRoleRepository = jpaRoleRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    public Role save(Role Role) {
        var entity = roleMapper.toEntity(Role);
        var savedEntity = jpaRoleRepository.save(entity);
        return roleMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Role> findById(String roleId) {
        return jpaRoleRepository.findById(roleId)
                .map(roleMapper::toDomain);
    }

    @Override
    public List<Role> findAll() {
        return jpaRoleRepository.findAll().stream()
                .map(roleMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String roleId) {
        jpaRoleRepository.deleteById(roleId);
    }

    @Override
    public Optional<Role> findByRoleName(RoleEnum roleName) {
        return jpaRoleRepository.findByRoleName(roleName)
                .map(roleMapper::toDomain);
    }

    @Override
    public boolean existsByRoleName(String roleName) {
        return jpaRoleRepository.existsByRoleName(roleName);
    }

}
