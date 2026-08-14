package com.example.backend_pj4.infrastructure.minio;

import java.io.InputStream;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.application.port.out.FileStorageService;
import com.example.backend_pj4.infrastructure.config.properties.MinioProperties;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MinioStorageAdapter implements FileStorageService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public MinioStorageAdapter(MinioClient minioClient, MinioProperties minioProperties) {
        this.minioClient = minioClient;
        this.minioProperties = minioProperties;
    }

    @Override
    public String upload(String bucket, String folder, String filename, InputStream data, long size, String contentType) {
        try {
            ensureBucketExists(bucket);
            String objectKey = folder + "/" + filename;
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectKey)
                    .stream(data, size, -1)
                    .contentType(contentType)
                    .build());
            return objectKey;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to MinIO", e);
        }
    }

    @Override
    public void delete(String bucket, String objectKey) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectKey)
                    .build());
        } catch (Exception e) {
            log.warn("Failed to delete object from MinIO: bucket={}, key={}", bucket, objectKey, e);
        }
    }

    @Override
    public String getPublicUrl(String bucket, String objectKey) {
        String base = minioProperties.getPublicEndpoint();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base + "/" + bucket + "/" + objectKey;
    }

    private void ensureBucketExists(String bucket) {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to ensure bucket exists: " + bucket, e);
        }
    }
}
