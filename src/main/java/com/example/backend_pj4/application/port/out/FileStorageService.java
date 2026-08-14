package com.example.backend_pj4.application.port.out;

import java.io.InputStream;

public interface FileStorageService {
    String upload(String bucket, String folder, String filename, InputStream data, long size, String contentType);
    void delete(String bucket, String objectKey);
    String getPublicUrl(String bucket, String objectKey);
}
