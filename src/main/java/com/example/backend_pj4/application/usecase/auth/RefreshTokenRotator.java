package com.example.backend_pj4.application.usecase.auth;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.backend_pj4.application.dto.auth.AuthTokenResult;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.AccountStatus;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.RefreshToken;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.RefreshTokenRepository;
import com.example.backend_pj4.domain.repository.UserRepository;
import com.example.backend_pj4.infrastructure.config.properties.JwtProperties;
import com.example.backend_pj4.infrastructure.security.JwtTokenProvider;

/**
 * Cơ chế xoay vòng refresh token, dùng chung cho cả hai kênh.
 * <p>
 * Trước đây {@code RefreshTokenService} và {@code AdminRefreshTokenService} là hai bản sao
 * của nhau. Bản admin được thêm check {@code isAdmin} còn bản user thì không — đó chính là
 * lỗ hổng cho phép cửa user phát ra vé admin. Gộp lại và đưa kênh thành tham số khiến hai
 * chiều không thể lệch nhau nữa.
 * <p>
 * Ranh giới giữa hai kênh vẫn giữ nguyên ở tầng trên: hai input port, hai use case,
 * hai controller, hai cookie riêng.
 */
@Service
public class RefreshTokenRotator {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;

    public RefreshTokenRotator(JwtTokenProvider jwtTokenProvider,
                               RefreshTokenRepository refreshTokenRepository,
                               UserRepository userRepository,
                               JwtProperties jwtProperties) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtProperties = jwtProperties;
    }

    /**
     * @param expectedChannel kênh mà endpoint gọi tới thuộc về. Token của kênh khác bị từ
     *                        chối theo cả hai chiều: cửa user không nhận token admin, cửa
     *                        admin không nhận token user.
     */
    public AuthTokenResult rotate(String rawRefreshToken,
                                  String expectedChannel,
                                  String userAgent,
                                  String ipAddress) {
        RefreshToken stored = loadValidToken(rawRefreshToken, expectedChannel);

        User user = userRepository.findById(stored.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // Không phát refresh token 30 ngày mới cho tài khoản đã bị vô hiệu hoá
        if (Boolean.TRUE.equals(user.getIsBanned()) || user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_REVOKED);
        }

        refreshTokenRepository.revokeByTokenId(stored.getTokenId());

        String newTokenId = UUID.randomUUID().toString();
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), expectedChannel);
        String newRefreshJwt = jwtTokenProvider.generateRefreshToken(user.getEmail(), newTokenId, expectedChannel);

        LocalDateTime now = LocalDateTime.now();
        refreshTokenRepository.save(RefreshToken.builder()
                .userId(user.getId())
                .tokenId(newTokenId)
                // sessionId giữ nguyên: rotation không mở phiên mới, vẫn là cùng một thiết bị
                .sessionId(stored.getSessionId())
                .admin(stored.isAdmin())
                .expiresAt(now.plusSeconds(jwtProperties.getRefreshExpiration() / 1000))
                .userAgent(userAgent != null ? userAgent : stored.getUserAgent())
                .ipAddress(ipAddress != null ? ipAddress : stored.getIpAddress())
                .lastUsedAt(now)
                .build());

        return new AuthTokenResult(newAccessToken, newRefreshJwt, jwtProperties.getExpiration());
    }

    /**
     * Verify chữ ký, loại token, hạn dùng, trạng thái thu hồi và kênh.
     * Dùng chung với {@link RefreshTokenRevoker} để logout áp cùng một bộ luật.
     */
    RefreshToken loadValidToken(String rawRefreshToken, String expectedChannel) {
        String tokenId;
        try {
            String tokenType = jwtTokenProvider.getTokenTypeFromToken(rawRefreshToken);
            tokenId = jwtTokenProvider.getTokenIdFromToken(rawRefreshToken);
            if (!"refresh".equals(tokenType) || tokenId == null) {
                tokenId = null;
            }
        } catch (Exception e) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID_OR_EXPIRED);
        }

        if (tokenId == null) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID_OR_EXPIRED);
        }
        if (jwtTokenProvider.isTokenExpired(rawRefreshToken)) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID_OR_EXPIRED);
        }

        RefreshToken stored = refreshTokenRepository.findByTokenId(tokenId)
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_REVOKED));

        boolean channelMismatch = !JwtTokenProvider.channelOf(stored.isAdmin()).equals(expectedChannel);
        if (stored.getRevokedAt() != null || channelMismatch) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_REVOKED);
        }

        return stored;
    }
}
