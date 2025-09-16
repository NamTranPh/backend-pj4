package com.example.backend_pj4.application.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backend_pj4.application.dto.request.LoginRequest;
import com.example.backend_pj4.application.dto.request.RegisterRequest;
import com.example.backend_pj4.application.dto.response.AuthResponse;
import com.example.backend_pj4.application.dto.response.UserResponse;
import com.example.backend_pj4.application.exceptions.ResourceNotFoundException;
import com.example.backend_pj4.domain.entities.Role;
import com.example.backend_pj4.domain.entities.User;
import com.example.backend_pj4.domain.repositories.RoleRepository;
import com.example.backend_pj4.domain.repositories.UserRepository;
import com.example.backend_pj4.infrastructure.security.JwtTokenProvider;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UserRepository userRepository; // Na ná hằng số
    private final RoleRepository roleRepository; // Na ná hằng số
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getPhone(),
                        request.getPassword()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String accesToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        User user = userRepository.findActiveUserByPhone(request.getPhone())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return new AuthResponse(accesToken, refreshToken, UserResponse.fromEntity(user));
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByPhone(request.getPhone()))
            throw new RuntimeException("Phone already exists");

        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new ResourceNotFoundException("User role not found"));

        User user = new User();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());

        user = userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getPhone());
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        return new AuthResponse(accessToken, refreshToken, UserResponse.fromEntity(user));
    }

    public AuthResponse refreshToken(String refreshToken) {
        try {
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtTokenProvider.validateToken(refreshToken, userDetails)) {
                String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);
                String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

                User user = userRepository.findActiveUserByPhone(username)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                return new AuthResponse(newAccessToken, newRefreshToken, UserResponse.fromEntity(user));
            } else {
                throw new RuntimeException("Invalid refresh token");
            }
        } catch (Exception e) {
            throw new RuntimeException("Invalid refresh token");
        }
    }
}
