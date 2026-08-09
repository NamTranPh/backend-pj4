package com.example.backend_pj4.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "auth.otp")
public class OtpProperties {
    private int length;
    private long ttlMinutes;
    private long resendCooldownSeconds;
    private int maxAttempts;
    private String pepper;
}
