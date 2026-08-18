package com.example.backend_pj4.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.backend_pj4.infrastructure.config.properties.MinioProperties;
import com.example.backend_pj4.infrastructure.minio.CustomMinioClient;

import io.minio.MinioAsyncClient;
import io.minio.MinioClient;

@Configuration
public class MinioConfig {

    @Bean
    MinioClient minioClient(MinioProperties properties) {
        return MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
    }

    @Bean
    CustomMinioClient customMinioClient(MinioProperties properties) {
        MinioAsyncClient asyncClient = MinioAsyncClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
        return new CustomMinioClient(asyncClient);
    }
}
