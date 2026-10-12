package com.example.backend_pj4.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "auth.rate-limit")
public class RateLimitProperties {
    private boolean enabled;
    /** Số request tối đa cho mỗi IP trong một cửa sổ. */
    private int maxRequests;
    private long windowSeconds;
}
