package com.example.backend_pj4.application.service;

import org.springframework.stereotype.Service;

import com.example.backend_pj4.application.port.out.FileStorageService;
import com.example.backend_pj4.infrastructure.config.properties.MinioProperties;

@Service
public class StorageUrlResolver {

    private static final int DEFAULT_EXPIRY_SECONDS = 3600;

    private final FileStorageService fileStorageService;
    private final MinioProperties minioProperties;

    public StorageUrlResolver(FileStorageService fileStorageService, MinioProperties minioProperties) {
        this.fileStorageService = fileStorageService;
        this.minioProperties = minioProperties;
    }

    public String resolvePublicImage(String objectKey) {
        return resolvePublic(minioProperties.getBucketPublic(), objectKey);
    }

    public String resolveAvatar(String objectKey) {
        return resolvePublic(minioProperties.getBucketAvatar(), objectKey);
    }

    public String resolveRaw(String objectKey) {
        return resolvePresigned(minioProperties.getBucketRaw(), objectKey);
    }

    private String resolvePublic(String bucket, String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            return null;
        }
        String key = normalizeObjectKey(objectKey, bucket);
        return fileStorageService.getPublicUrl(bucket, key);
    }

    private String resolvePresigned(String bucket, String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            return null;
        }
        String key = normalizeObjectKey(objectKey, bucket);
        return fileStorageService.getPresignedGetUrl(bucket, key, DEFAULT_EXPIRY_SECONDS);
    }

    /**
     * Strips full URL prefix if DB has legacy data with full URLs.
     * e.g. "http://localhost:9000/movie-public/movies/x/poster.jpg" → "movies/x/poster.jpg"
     */
    private String normalizeObjectKey(String value, String bucket) {
        if (value.startsWith("http://") || value.startsWith("https://")) {
            String marker = "/" + bucket + "/";
            int idx = value.indexOf(marker);
            if (idx >= 0) {
                return value.substring(idx + marker.length());
            }
        }
        return value;
    }
}
