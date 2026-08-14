package com.example.backend_pj4.application.usecase.auth;

import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.auth.RefreshTokenCommand;
import com.example.backend_pj4.application.dto.auth.AuthTokenResult;
import com.example.backend_pj4.application.port.in.auth.RefreshTokenUseCase;
import com.example.backend_pj4.application.port.out.RefreshTokenStore;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.RefreshToken;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.UserRepository;
import com.example.backend_pj4.infrastructure.config.properties.JwtProperties;
import com.example.backend_pj4.infrastructure.security.CustomUserDetailsService;
import com.example.backend_pj4.infrastructure.security.JwtTokenProvider;

import java.time.LocalDateTime;

@Service
public class RefreshTokenService implements RefreshTokenUseCase {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final RefreshTokenStore refreshTokenStore;
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;

    public RefreshTokenService(JwtTokenProvider jwtTokenProvider,
                                CustomUserDetailsService userDetailsService,
                                RefreshTokenStore refreshTokenStore,
                                UserRepository userRepository,
                                JwtProperties jwtProperties) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.refreshTokenStore = refreshTokenStore;
        this.userRepository = userRepository;
        this.jwtProperties = jwtProperties;
    }

    @Override
    @Transactional
    public AuthTokenResult execute(RefreshTokenCommand command) {
        String rawToken = command.rawRefreshToken();

        String tokenType;
        String tokenId;
        String subject;
        try {
            tokenType = jwtTokenProvider.getTokenTypeFromToken(rawToken);
            tokenId = jwtTokenProvider.getTokenIdFromToken(rawToken);
            subject = jwtTokenProvider.getSubjectFromToken(rawToken);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID_OR_EXPIRED);
        }

        if (!"refresh".equals(tokenType) || tokenId == null) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID_OR_EXPIRED);
        }

        if (jwtTokenProvider.isTokenExpired(rawToken)) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID_OR_EXPIRED);
        }

        RefreshToken stored = refreshTokenStore.findByTokenId(tokenId)
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_REVOKED));

        if (stored.getRevokedAt() != null) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_REVOKED);
        }

        refreshTokenStore.revokeByTokenId(tokenId);

        User user = userRepository.findById(stored.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
        String newTokenId = UUID.randomUUID().toString();
        String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String newRefreshJwt = jwtTokenProvider.generateRefreshToken(userDetails, newTokenId);

        RefreshToken newRt = RefreshToken.builder()
                .userId(user.getId())
                .tokenId(newTokenId)
                .admin(stored.isAdmin())
                .expiresAt(LocalDateTime.now().plusSeconds(jwtProperties.getRefreshExpiration() / 1000))
                .build();
        refreshTokenStore.save(newRt);

        return new AuthTokenResult(newAccessToken, newRefreshJwt, jwtProperties.getExpiration());
    }
}
