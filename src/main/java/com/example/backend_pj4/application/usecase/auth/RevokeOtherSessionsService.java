package com.example.backend_pj4.application.usecase.auth;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.port.in.auth.RevokeOtherSessionsUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.RefreshToken;
import com.example.backend_pj4.domain.repository.RefreshTokenRepository;
import com.example.backend_pj4.infrastructure.security.JwtTokenProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RevokeOtherSessionsService implements RevokeOtherSessionsUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public RevokeOtherSessionsService(RefreshTokenRepository refreshTokenRepository,
                                     JwtTokenProvider jwtTokenProvider) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    @Transactional
    public void execute(String userId, String rawRefreshToken) {
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID_OR_EXPIRED);
        }

        String tokenId;
        try {
            tokenId = jwtTokenProvider.getTokenIdFromToken(rawRefreshToken);
        } catch (Exception e) {
            log.warn("Không thể giải mã tokenId từ refresh token: {}", e.getMessage());
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID_OR_EXPIRED);
        }

        if (tokenId == null || tokenId.isBlank()) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID_OR_EXPIRED);
        }

        RefreshToken currentToken = refreshTokenRepository.findByTokenId(tokenId)
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_INVALID_OR_EXPIRED));

        if (!userId.equals(currentToken.getUserId())) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID_OR_EXPIRED);
        }

        if (currentToken.getRevokedAt() != null
                || (currentToken.getExpiresAt() != null && currentToken.getExpiresAt().isBefore(LocalDateTime.now()))) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID_OR_EXPIRED);
        }

        String currentSessionId = currentToken.getSessionId();
        if (currentSessionId == null || currentSessionId.isBlank()) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID_OR_EXPIRED);
        }

        refreshTokenRepository.revokeAllByUserIdExceptSession(userId, currentSessionId);
        log.info("Đã đăng xuất tất cả các thiết bị khác của userId={}, giữ lại sessionId={}", userId, currentSessionId);
    }
}
