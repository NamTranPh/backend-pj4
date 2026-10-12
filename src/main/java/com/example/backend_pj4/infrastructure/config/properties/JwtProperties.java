package com.example.backend_pj4.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret;
    private long expiration;
    private long refreshExpiration;
    private String issuer;
    /**
     * Cửa sổ ân hạn khi phát hiện dùng lại refresh token đã thu hồi. Hai tab refresh song song
     * là chuyện bình thường, không phải bị trộm — trong khoảng này thì không thu hồi cả phiên.
     */
    private long reuseGraceSeconds;
}
