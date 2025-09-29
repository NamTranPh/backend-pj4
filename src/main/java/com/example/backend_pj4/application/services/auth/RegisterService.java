package com.example.backend_pj4.application.services.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backend_pj4.application.dto.request.RegisterRequest;
import com.example.backend_pj4.application.dto.response.AuthResponse;
import com.example.backend_pj4.application.exceptions.ResourceNotFoundException;
import com.example.backend_pj4.domain.enums.MembershipStatus;
import com.example.backend_pj4.infrastructure.databases.entities.RoleEntity;
import com.example.backend_pj4.infrastructure.databases.entities.UserEntity;
import com.example.backend_pj4.infrastructure.databases.repository.JpaRoleRepository;
import com.example.backend_pj4.infrastructure.databases.repository.JpaUserRepository;
import com.example.backend_pj4.infrastructure.security.JwtTokenProvider;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RegisterService {
    private final JpaUserRepository userRepository;
    private final JpaRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone already exists");
        }

        RoleEntity userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new ResourceNotFoundException("User role not found"));

        UserEntity user = new UserEntity();
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(userRole);
        user.setMembershipStatus(MembershipStatus.FREE);
        user.setIsActive(true);

        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getPhone());
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        return AuthResponse.success(user.getPhone(), accessToken, refreshToken);
    }
}
