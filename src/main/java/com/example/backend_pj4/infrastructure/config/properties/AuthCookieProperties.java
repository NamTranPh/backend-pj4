package com.example.backend_pj4.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "auth.cookie")
public class AuthCookieProperties {
    private String accessName;
    private String refreshName;
    private String domain;
    private boolean secure;
    private String sameSite;
    private String path;
    private String refreshPath;
    private String adminRefreshName;
    private String adminRefreshPath;
}
