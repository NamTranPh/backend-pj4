package com.example.backend_pj4.infrastructure.config.properties;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * TTL của các cache nghiệp vụ. đọc .env, qua application.yml làm trung gian.
 */
@Data
@ConfigurationProperties(prefix = "app.cache")
public class CacheProperties {
    private Duration commentListTtl;
    private Duration ratingSummaryTtl;
    private long likeFlushIntervalMs;
}
