package com.example.backend_pj4.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "app.payos")
public class PayOsProperties {
    private String clientId;
    private String apiKey;
    private String checksumKey;
    private String baseUrl;
    private String returnUrl;
    private String cancelUrl;
    private int paymentLinkTtlMinutes;
    private long reconcileIntervalMs;
    private int reconcileBatchLimit;
    private int reconcileLookbackHours;
}
