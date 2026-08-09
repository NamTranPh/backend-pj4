package com.example.backend_pj4.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "auth.login")
public class LoginLockProperties {
    private int maxFailures;
    private long failureWindowMinutes;
    private long lockMinutes;
}
