package com.example.backend_pj4.infrastructure.jobs;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.repository.OtpVerificationRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Dọn OTP đã hết hạn.
 * <p>Chạy lúc 03:00 để lệch với {@link UploadSessionCleanupJob} (02:00) và
 * {@link RefreshTokenCleanupJob} (02:30).
 */
@Slf4j
@Component
public class OtpCleanupJob {

    private final OtpVerificationRepository otpVerificationRepository;

    public OtpCleanupJob(OtpVerificationRepository otpVerificationRepository) {
        this.otpVerificationRepository = otpVerificationRepository;
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void cleanupExpiredOtp() {
        try {
            int deleted = otpVerificationRepository.deleteExpired();
            if (deleted > 0) {
                log.info("Đã xoá {} OTP hết hạn", deleted);
            }
        } catch (Exception e) {
            log.error("Dọn OTP hết hạn thất bại", e);
        }
    }
}
