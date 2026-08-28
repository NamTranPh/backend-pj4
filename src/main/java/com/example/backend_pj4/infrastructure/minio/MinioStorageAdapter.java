package com.example.backend_pj4.infrastructure.minio;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.application.port.out.FileStorageService;
import com.example.backend_pj4.infrastructure.config.properties.MinioProperties;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import io.minio.GetObjectArgs;
import io.minio.BucketExistsArgs;
import io.minio.CreateMultipartUploadResponse;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.SetBucketPolicyArgs;
import io.minio.http.Method;
import io.minio.messages.Part;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MinioStorageAdapter implements FileStorageService {

    private final MinioClient minioClient;
    private final CustomMinioClient customMinioClient;
    private final MinioProperties minioProperties;

    public MinioStorageAdapter(MinioClient minioClient, CustomMinioClient customMinioClient, MinioProperties minioProperties) {
        this.minioClient = minioClient;
        this.customMinioClient = customMinioClient;
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

    @Override
    public String getPresignedGetUrl(String bucket, String objectKey, int expirySeconds) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucket)
                    .object(objectKey)
                    .expiry(expirySeconds, TimeUnit.SECONDS)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate presigned GET URL", e);
        }
    }

    @Override
    public InputStream getObject(String bucket, String key) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get object from MinIO: bucket=" + bucket + ", key=" + key, e);
        }
    }

    @Override
    public String initiateMultipartUpload(String bucket, String objectKey) {
        try {
            ensureBucketExists(bucket);
            Multimap<String, String> headers = HashMultimap.create();
            headers.put("Content-Type", "application/octet-stream");
            CreateMultipartUploadResponse response = customMinioClient
                    .initMultipartUpload(bucket, null, objectKey, headers, null)
                    .get();
            return response.result().uploadId();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initiate multipart upload", e);
        }
    }

    @Override
    public String getPresignedUploadUrl(String bucket, String objectKey, String uploadId, int partNumber, int expirySeconds) {
        try {
            Map<String, String> queryParams = new HashMap<>();
            queryParams.put("uploadId", uploadId);
            queryParams.put("partNumber", String.valueOf(partNumber));

            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.PUT)
                    .bucket(bucket)
                    .object(objectKey)
                    .expiry(expirySeconds, TimeUnit.SECONDS)
                    .extraQueryParams(queryParams)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }

    @Override
    public void completeMultipartUpload(String bucket, String objectKey, String uploadId, List<PartETag> parts) {
        try {
            Part[] minioParts = parts.stream()
                    .map(p -> new Part(p.partNumber(), p.etag()))
                    .toArray(Part[]::new);
            customMinioClient.mergeMultipartUpload(bucket, null, objectKey, uploadId, minioParts, null, null).get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to complete multipart upload", e);
        }
    }

    @Override
    public void abortMultipartUpload(String bucket, String objectKey, String uploadId) {
        try {
            customMinioClient.cancelMultipartUpload(bucket, null, objectKey, uploadId, null, null).get();
        } catch (Exception e) {
            log.warn("Failed to abort multipart upload: bucket={}, key={}, uploadId={}", bucket, objectKey, uploadId, e);
        }
    }

    private void ensureBucketExists(String bucket) {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
            if (isPublicBucket(bucket)) {
                setPublicBucketPolicy(bucket);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to ensure bucket exists: " + bucket, e);
        }
    }

    private boolean isPublicBucket(String bucket) {
        return bucket != null && (bucket.equals(minioProperties.getBucketPublic()) || bucket.equals(minioProperties.getBucketAvatar()));
    }

    private void setPublicBucketPolicy(String bucket) {
        try {
            String policy = """
                {
                  "Version": "2012-10-17",
                  "Statement": [
                    {
                      "Effect": "Allow",
                      "Principal": {"AWS": ["*"]},
                      "Action": ["s3:GetObject"],
                      "Resource": ["arn:aws:s3:::%s/*"]
                    }
                  ]
                }
                """.formatted(bucket);
            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucket).config(policy).build());
        } catch (Exception e) {
            log.warn("Failed to set public bucket policy for bucket: {}", bucket, e);
        }
    }
}
