package com.example.backend_pj4.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "app.playback")
public class PlaybackProperties {
    /**
     * Khoá ký playback token. Mặc định khai ở {@code application.yml}, <b>không</b> đặt ở đây.
     *
     * <p>Trước đây field này có sẵn giá trị {@code "change-this-playback-secret-in-production"},
     * nên khi cấu hình không tới nơi thì hệ thống vẫn chạy — ký token bằng một chuỗi nằm công khai
     * trong git. Ai đọc source là tự ký được token hợp lệ. Để mặc định ở file .java khiến lỗi cấu
     * hình trở nên vô hình; để ở yml thì {@code ConfigurationWarningLogger} báo được.
     */
    private String tokenSecret;

    private int tokenTtlSeconds;
}
