package com.example.backend_pj4.application.port.out;

import java.io.InputStream;
import java.util.List;

public interface FileStorageService {
    String upload(String bucket, String folder, String filename, InputStream data, long size, String contentType);
    void delete(String bucket, String objectKey);
    String getPublicUrl(String bucket, String objectKey);

    String initiateMultipartUpload(String bucket, String objectKey);
    String getPresignedUploadUrl(String bucket, String objectKey, String uploadId, int partNumber, int expirySeconds);
    void completeMultipartUpload(String bucket, String objectKey, String uploadId, List<PartETag> parts);
    void abortMultipartUpload(String bucket, String objectKey, String uploadId);

    record PartETag(int partNumber, String etag) {}
}
