package com.example.backend_pj4.application.services.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.example.backend_pj4.application.dto.response.auth.ResponseAuthDto;
import com.example.backend_pj4.application.exceptions.ResourceNotFoundException;
import com.example.backend_pj4.infrastructure.databases.repository.JpaUserRepository;
import com.example.backend_pj4.infrastructure.security.JwtTokenProvider;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


//Chưa xong
@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {
    private final JpaUserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public ResponseAuthDto refreshToken(String refreshToken) {
    String username = jwtTokenProvider.getPhoneFromToken(refreshToken);
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    if (!jwtTokenProvider.validateToken(refreshToken, userDetails)) {
    throw new RuntimeException("Invalid refresh token");
    }

    String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);
    String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

    userRepository.findByPhone(username)
    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    return ResponseAuthDto.success(username, newAccessToken, newRefreshToken);
    }
}
