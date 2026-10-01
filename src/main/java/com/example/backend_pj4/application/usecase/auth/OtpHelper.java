package com.example.backend_pj4.application.usecase.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HexFormat;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.application.port.out.OtpSender;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.OtpType;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.OtpVerification;
import com.example.backend_pj4.domain.repository.OtpVerificationRepository;
import com.example.backend_pj4.infrastructure.config.properties.OtpProperties;

@Component
public class OtpHelper {

    private final OtpVerificationRepository otpRepository;
    private final OtpSender otpSender;
    private final OtpProperties otpProperties;
    private final SecureRandom secureRandom = new SecureRandom();

    public OtpHelper(OtpVerificationRepository otpRepository,
                     OtpSender otpSender,
                     OtpProperties otpProperties) {
        this.otpRepository = otpRepository;
        this.otpSender = otpSender;
        this.otpProperties = otpProperties;
    }

    public void issueOtp(String email, OtpType type) {
        otpRepository.findByEmailAndType(email, type)
                .ifPresent(otp -> otpRepository.deleteById(otp.getId()));

        String code = generateCode();
        String hash = hashOtp(code, email);

        OtpVerification otp = OtpVerification.builder()
                .email(email.toLowerCase().trim())
                .codeHash(hash)
                .type(type)
                .expiresAt(LocalDateTime.now().plusMinutes(otpProperties.getTtlMinutes()))
                .build();
        otpRepository.save(otp);
        otpSender.send(email, code, type);
    }

    /**
     * Chặn phát OTP mới quá sớm. Gọi trước mọi lần {@link #issueOtp} phát sinh từ request
     * của client, kể cả từ /register — nếu không, gọi lại /register là spam được email.
     */
    public void assertResendCooldown(String email, OtpType type) {
        if (isWithinResendCooldown(email, type)) {
            throw new CustomException(ErrorCode.OTP_RESEND_TOO_SOON);
        }
    }

    /**
     * Biến thể không ném exception, dùng cho luồng phải giữ "silent success" như
     * forgot-password (ném lỗi ở đó sẽ tiết lộ email nào có tài khoản).
     */
    public boolean isWithinResendCooldown(String email, OtpType type) {
        return otpRepository.findByEmailAndType(email, type)
                .filter(existing -> existing.getCreatedAt() != null)
                .map(existing -> Duration.between(existing.getCreatedAt(), LocalDateTime.now()).getSeconds()
                        < otpProperties.getResendCooldownSeconds())
                .orElse(false);
    }

    public String hashOtp(String code, String email) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String raw = code + ":" + email.toLowerCase().trim() + ":" + otpProperties.getPepper();
            return HexFormat.of().formatHex(digest.digest(raw.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash OTP", e);
        }
    }

    private String generateCode() {
        int bound = (int) Math.pow(10, otpProperties.getLength());
        return String.format("%0" + otpProperties.getLength() + "d", secureRandom.nextInt(bound));
    }
}
