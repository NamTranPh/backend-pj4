package com.example.backend_pj4.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    /** Endpoint để BACKEND gọi MinIO (localhost:9000 khi chạy máy, minio:9000 khi chạy docker). */
    private String endpoint;

    /** Endpoint TRÌNH DUYỆT dùng để tải file (luôn là địa chỉ host). */
    private String publicEndpoint;

    private String accessKey;
    private String secretKey;

    /** Bucket chứa video gốc vừa upload. */
    private String bucketRaw;

    /** Bucket chứa file HLS sau khi transcode. */
    private String bucketProcessed;

    /** Bucket public (poster, thumbnail) cho phép đọc trực tiếp. */
    private String bucketPublic;

    /** Bucket chứa avatar/profile images. */
    private String bucketAvatar;
}
