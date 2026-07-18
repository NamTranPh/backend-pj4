package com.example.backend_pj4.application.services.user_cms;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.backend_pj4.application.dto.request.user_cms.RequestCreateUserCmsDto;
import com.example.backend_pj4.application.exceptions.ResourceNotFoundException;
import com.example.backend_pj4.common.base.BaseService;
import com.example.backend_pj4.common.enums.RoleEnum;
import com.example.backend_pj4.common.utils.PasswordUtils;
import com.example.backend_pj4.domain.entities.User;
import com.example.backend_pj4.domain.repository.RoleRepository;
import com.example.backend_pj4.domain.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateUserService extends BaseService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public User execute(RequestCreateUserCmsDto request) {
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("Phone already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        var defaultRole = roleRepository.findByRoleName(RoleEnum.STAFF)
                .orElseThrow(() -> new ResourceNotFoundException("Default role not found"));

        User user = User.builder()
                .email(request.getEmail())
                .password(PasswordUtils.hashPassword(request.getPassword()))
                .name(request.getName())
                .phone(request.getPhone())
                .birthDate(request.getBirthDate())
                .profilePicture(request.getProfilePicture())
                .address(request.getAddress())
                .role(defaultRole)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }
}