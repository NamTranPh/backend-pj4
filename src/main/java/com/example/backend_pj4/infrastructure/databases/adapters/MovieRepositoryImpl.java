package com.example.backend_pj4.infrastructure.databases.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.domain.entities.Role;
import com.example.backend_pj4.domain.repository.RoleRepository;
import com.example.backend_pj4.infrastructure.databases.mapper.RoleMapper;
import com.example.backend_pj4.infrastructure.databases.repository.JpaRoleRepository;

@Repository 
public class MovieRepositoryImpl implements RoleRepository {
    private final JpaRoleRepository jpaRoleRepository;

    public MovieRepositoryImpl(JpaRoleRepository jpaRoleRepository) {
        this.jpaRoleRepository = jpaRoleRepository;
    }

    @Override
    public Role save(Role Role) {
        var entity = RoleMapper.toEntity(Role);
        var savedEntity = jpaRoleRepository.save(entity);
        return RoleMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Role> findById(String roleId) {
        return jpaRoleRepository.findById(roleId)
                .map(RoleMapper::toDomain);
    }

    @Override
    public List<Role> findAll() {
        return jpaRoleRepository.findAll().stream()
                .map(RoleMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String roleId) {
        jpaRoleRepository.deleteById(roleId);
    }

    @Override
    public Optional<Role> findByRoleName(String roleName) {
        return jpaRoleRepository.findByRoleName(roleName)
                .map(RoleMapper::toDomain);
    }

    @Override
    public boolean existsByRoleName(String roleName) {
        return jpaRoleRepository.existsByRoleName(roleName);
    }

}
