package com.example.backend_pj4.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.backend_pj4.infrastructure.config.properties.MinioProperties;

import io.minio.MinioClient;

@Configuration
public class MinioConfig {

    /**
     * MinioClient là bean dùng chung để thao tác object storage.
     * Client khởi tạo lazy (không mở kết nối lúc startup) nên app vẫn boot
     * được kể cả khi MinIO chưa chạy.
     */
    @Bean
    MinioClient minioClient(MinioProperties properties) {
        return MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
    }
}
