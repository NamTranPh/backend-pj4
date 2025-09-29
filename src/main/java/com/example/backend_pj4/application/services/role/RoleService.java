package com.example.backend_pj4.application.services.role;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend_pj4.application.exceptions.ResourceNotFoundException;
import com.example.backend_pj4.domain.entities.Role;
import com.example.backend_pj4.domain.repository.RoleRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleService {
    private final RoleRepository roleRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    // Co the se phai phat trien them
    public Role getRoleById(String roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));
    }

    public Role creatRRole(Role role) {
        if (roleRepository.existsByRoleName(role.getRoleName())) {
            throw new IllegalArgumentException("Role already exists");
        }

        return roleRepository.save(role);
    }

    public Role updateRole(Role role) {
        if (role.getRoleId() == null || roleRepository.findById(role.getRoleId()).isEmpty()) {
            throw new IllegalArgumentException("Role not found with ID: " + role.getRoleId());
        }
        return roleRepository.save(role);
    }

    public void deleteRole(String roleId) {
        roleRepository.deleteById(roleId);
    }

    // Kiểm tra tồn tại theo tên
    public boolean existsByRoleName(String roleName) {
        return roleRepository.existsByRoleName(roleName);
    }
}
