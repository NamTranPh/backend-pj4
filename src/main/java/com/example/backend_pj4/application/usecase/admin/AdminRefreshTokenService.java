package com.example.backend_pj4.application.usecase.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.auth.RefreshTokenCommand;
import com.example.backend_pj4.application.dto.auth.AuthTokenResult;
import com.example.backend_pj4.application.port.in.admin.AdminRefreshTokenUseCase;
import com.example.backend_pj4.application.port.out.RefreshTokenStore;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.RefreshToken;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.UserRepository;
import com.example.backend_pj4.infrastructure.config.properties.JwtProperties;
import com.example.backend_pj4.infrastructure.security.CustomUserDetailsService;
import com.example.backend_pj4.infrastructure.security.JwtTokenProvider;

@Service
public class AdminRefreshTokenService implements AdminRefreshTokenUseCase {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final RefreshTokenStore refreshTokenStore;
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;

    public AdminRefreshTokenService(JwtTokenProvider jwtTokenProvider,
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

        String tokenId;
        String subject;
        try {
            String tokenType = jwtTokenProvider.getTokenTypeFromToken(rawToken);
            tokenId = jwtTokenProvider.getTokenIdFromToken(rawToken);
            subject = jwtTokenProvider.getSubjectFromToken(rawToken);
            if (!"refresh".equals(tokenType) || tokenId == null) {
                throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID_OR_EXPIRED);
            }
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID_OR_EXPIRED);
        }

        if (jwtTokenProvider.isTokenExpired(rawToken)) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID_OR_EXPIRED);
        }

        RefreshToken stored = refreshTokenStore.findByTokenId(tokenId)
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_REVOKED));

        if (stored.getRevokedAt() != null || !stored.isAdmin()) {
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
                .admin(true)
                .expiresAt(LocalDateTime.now().plusSeconds(jwtProperties.getRefreshExpiration() / 1000))
                .build();
        refreshTokenStore.save(newRt);

        return new AuthTokenResult(newAccessToken, newRefreshJwt, jwtProperties.getExpiration());
    }
}
