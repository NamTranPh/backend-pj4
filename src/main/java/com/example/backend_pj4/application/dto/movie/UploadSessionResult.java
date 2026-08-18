package com.example.backend_pj4.application.dto.movie;

import java.time.LocalDateTime;
import java.util.List;

public record UploadSessionResult(
        String id,
        String targetId,
        String uploadId,
        String rawFileKey,
        String bucket,
        long fileSizeBytes,
        String fileName,
        long partSizeBytes,
        String status,
        List<UploadPartResult> parts,
        LocalDateTime expiresAt,
        LocalDateTime createdAt
) {
}
