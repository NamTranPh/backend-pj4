package com.example.backend_pj4.application.services.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.backend_pj4.application.dto.request.auth.RequestLoginDto;
import com.example.backend_pj4.application.dto.response.auth.ResponseAuthDto;
import com.example.backend_pj4.application.exceptions.ResourceNotFoundException;
import com.example.backend_pj4.common.base.BaseService;
import com.example.backend_pj4.domain.repository.UserRepository;
import com.example.backend_pj4.infrastructure.security.JwtTokenProvider;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginService extends BaseService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public ResponseAuthDto login(RequestLoginDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getPhone(),
                        request.getPassword()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        // check user exists
        userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return ResponseAuthDto.success(request.getPhone(), accessToken, refreshToken);
    }
}