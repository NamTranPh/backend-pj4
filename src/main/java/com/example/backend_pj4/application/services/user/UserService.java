package com.example.backend_pj4.application.services.user;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend_pj4.application.exceptions.ResourceNotFoundException;
import com.example.backend_pj4.domain.entities.User;
import com.example.backend_pj4.domain.repository.RoleRepository;
import com.example.backend_pj4.domain.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    public User createUser(User user) {
        // Validate
        if (userRepository.existsByPhone(user.getPhone())) {
            throw new IllegalArgumentException("Phone already exists");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Set defaults - Thao tác với Domain Object
        var defaultRole = roleRepository.findByRoleName("STAFF")
                .orElseThrow(() -> new ResourceNotFoundException("Default role USER not found"));

        // Build user với defaults
        var userToSave = user.toBuilder()
                .isActive(true)
                .role(defaultRole)
                .build();

        return userRepository.save(userToSave);
    }

    public User updateUser(String id, User partial) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Validate phone uniqueness
        if (partial.getPhone() != null &&
                !existing.getPhone().equals(partial.getPhone()) &&
                userRepository.existsByPhone(partial.getPhone())) {
            throw new IllegalArgumentException("Phone already exists");
        }

        // Validate email uniqueness
        if (partial.getEmail() != null &&
                !existing.getEmail().equals(partial.getEmail()) &&
                userRepository.existsByEmail(partial.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Update fields - Sử dụng Builder pattern
        var updated = existing.toBuilder()
                .name(partial.getName() != null ? partial.getName() : existing.getName())
                .email(partial.getEmail() != null ? partial.getEmail() : existing.getEmail())
                .phone(partial.getPhone() != null ? partial.getPhone() : existing.getPhone())
                .birthDate(partial.getBirthDate() != null ? partial.getBirthDate() : existing.getBirthDate())
                .profilePicture(partial.getProfilePicture() != null ? partial.getProfilePicture()
                        : existing.getProfilePicture())
                .address(partial.getAddress() != null ? partial.getAddress() : existing.getAddress())
                .build();

        return userRepository.save(updated);
    }

    public void deleteUser(String id) {
        if (!userRepository.findById(id).isPresent()) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }
}