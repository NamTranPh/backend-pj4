package com.example.backend_pj4.application.usecase.auth;

import org.springframework.stereotype.Service;

import com.example.backend_pj4.domain.model.RefreshToken;
import com.example.backend_pj4.domain.repository.RefreshTokenRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Cơ chế thu hồi refresh token khi logout, dùng chung cho cả hai kênh.
 * Trước đây {@code LogoutService} và {@code AdminLogoutService} giống hệt nhau từng dòng.
 */
@Slf4j
@Service
public class RefreshTokenRevoker {

    private final RefreshTokenRotator rotator;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenRevoker(RefreshTokenRotator rotator,
                               RefreshTokenRepository refreshTokenRepository) {
        this.rotator = rotator;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * Logout là thao tác idempotent: token không hợp lệ, đã hết hạn hoặc sai kênh thì
     * không có gì để thu hồi, và cookie phía controller vẫn được xoá. Nuốt lỗi ở đây là
     * có chủ đích, không phải che lỗi — người dùng luôn đăng xuất được.
     */
    public void revoke(String rawRefreshToken, String expectedChannel) {
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            return;
        }

        try {
            RefreshToken stored = rotator.loadValidToken(rawRefreshToken, expectedChannel);
            refreshTokenRepository.revokeByTokenId(stored.getTokenId());
        } catch (Exception e) {
            log.warn("Bỏ qua thu hồi refresh token khi logout (channel={}): {}",
                    expectedChannel, e.getMessage());
        }
    }
}
