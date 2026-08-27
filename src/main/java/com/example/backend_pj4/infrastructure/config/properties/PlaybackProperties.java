package com.example.backend_pj4.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "app.playback")
public class PlaybackProperties {
    private String tokenSecret = "change-this-playback-secret-in-production";
    private int tokenTtlSeconds = 300;
}
