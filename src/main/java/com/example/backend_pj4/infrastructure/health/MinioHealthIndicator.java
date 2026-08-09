package com.example.backend_pj4.infrastructure.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import com.example.backend_pj4.infrastructure.config.properties.MinioProperties;

import io.minio.BucketExistsArgs;
import io.minio.MinioClient;

import lombok.RequiredArgsConstructor;

/**
 * Health check cho MinIO. Bean tên "minioHealthIndicator" -> key "minio"
 * trong kết quả /actuator/health.
 */
@Component
@RequiredArgsConstructor
public class MinioHealthIndicator implements HealthIndicator {

    private final MinioClient minioClient;
    private final MinioProperties properties;

    @Override
    public Health health() {
        try {
            // Thao tác nhẹ để kiểm tra kết nối tới MinIO (không tạo/xoá gì).
            minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(properties.getBucketRaw())
                            .build());
            return Health.up()
                    .withDetail("endpoint", properties.getEndpoint())
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("endpoint", properties.getEndpoint())
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
