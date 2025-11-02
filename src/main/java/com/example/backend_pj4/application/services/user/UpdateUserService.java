package com.example.backend_pj4.application.services.user;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.request.user_cms.RequestUpdateUserDto;
import com.example.backend_pj4.application.exceptions.ResourceNotFoundException;
import com.example.backend_pj4.common.base.BaseService;
import com.example.backend_pj4.common.utils.PasswordUtils;
import com.example.backend_pj4.domain.entities.User;
import com.example.backend_pj4.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateUserService extends BaseService {

    private final UserRepository userRepository;

    @Transactional
    public User execute(String userId, RequestUpdateUserDto dto) {
        User existing = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Validate phone uniqueness
        if (dto.getPhone() != null &&
                !existing.getPhone().equals(dto.getPhone()) &&
                userRepository.existsByPhone(dto.getPhone())) {
            throw new IllegalArgumentException("Phone already exists");
        }

        // Validate email uniqueness
        if (dto.getEmail() != null &&
                !existing.getEmail().equals(dto.getEmail()) &&
                userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Hash password nếu có
        String updatedPassword = existing.getPassword();
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            updatedPassword = PasswordUtils.hashPassword(dto.getPassword());
        }
        // Update fields
        var updated = existing.toBuilder()
                .name(dto.getName() != null ? dto.getName() : existing.getName())
                .email(dto.getEmail() != null ? dto.getEmail() : existing.getEmail())
                .phone(dto.getPhone() != null ? dto.getPhone() : existing.getPhone())
                .password(updatedPassword)
                .birthDate(dto.getBirthDate() != null ? dto.getBirthDate() : existing.getBirthDate())
                .profilePicture(
                        dto.getProfilePicture() != null ? dto.getProfilePicture() : existing.getProfilePicture())
                .address(dto.getAddress() != null ? dto.getAddress() : existing.getAddress())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : existing.getIsActive())
                .updatedAt(LocalDateTime.now())
                .build();

        return userRepository.save(updated);
    }
}
