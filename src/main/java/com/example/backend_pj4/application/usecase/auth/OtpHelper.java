package com.example.backend_pj4.application.usecase.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.application.port.out.OtpSender;
import com.example.backend_pj4.common.constants.enums.OtpType;
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

    public String hashOtp(String code, String email) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String raw = code + ":" + email.toLowerCase().trim() + ":" + otpProperties.getPepper();
            return HexFormat.of().formatHex(digest.digest(raw.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash OTP", e);
        }
    }

    public long getResendCooldownSeconds() {
        return otpProperties.getResendCooldownSeconds();
    }

    private String generateCode() {
        int bound = (int) Math.pow(10, otpProperties.getLength());
        return String.format("%0" + otpProperties.getLength() + "d", secureRandom.nextInt(bound));
    }
}
