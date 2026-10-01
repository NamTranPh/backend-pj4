package com.example.backend_pj4.infrastructure.jobs;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.repository.RefreshTokenRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Dọn refresh token đã hết hạn. Rotation sinh một row mới mỗi 15 phút cho mỗi phiên
 * (~96 row/ngày/người dùng); không có job này thì bảng phình vĩnh viễn.
 * <p>
 * Chạy 02:30 để lệch với {@link UploadSessionCleanupJob} (02:00).
 */
@Slf4j
@Component
public class RefreshTokenCleanupJob {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenCleanupJob(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Scheduled(cron = "0 30 2 * * *")
    public void cleanupExpiredRefreshTokens() {
        try {
            int deleted = refreshTokenRepository.deleteExpired();
            if (deleted > 0) {
                log.info("Đã xoá {} refresh token hết hạn", deleted);
            }
        } catch (Exception e) {
            log.error("Dọn refresh token hết hạn thất bại", e);
        }
    }
}
