package com.example.backend_pj4.application.service;

import java.util.function.Function;

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

    public String resolveActorAvatar(String objectKey) {
        return resolvePublic(minioProperties.getBucketActor(), objectKey);
    }

    public String resolveRaw(String objectKey) {
        return resolvePresigned(minioProperties.getBucketRaw(), objectKey);
    }

    private String resolve(String bucket, String objectKey, Function<String, String> urlBuilder) {
        if (objectKey == null || objectKey.isBlank()) {
            return null;
        }

        if (objectKey.startsWith("http://") || objectKey.startsWith("https://")) {
            String marker = "/" + bucket + "/";
            int idx = objectKey.indexOf(marker);
            if (idx >= 0) {
                String key = objectKey.substring(idx + marker.length());
                return urlBuilder.apply(key);
            }
            return objectKey;
        }
        return urlBuilder.apply(objectKey);
    }

    private String resolvePublic(String bucket, String objectKey) {
        return resolve(bucket, objectKey, key -> fileStorageService.getPublicUrl(bucket, key));
    }

    private String resolvePresigned(String bucket, String objectKey) {
        return resolve(bucket, objectKey,
                key -> fileStorageService.getPresignedGetUrl(bucket, key, DEFAULT_EXPIRY_SECONDS));
    }
}