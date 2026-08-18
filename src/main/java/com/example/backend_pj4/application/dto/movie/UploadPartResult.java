package com.example.backend_pj4.application.dto.movie;

import java.time.LocalDateTime;

public record UploadPartResult(
        int partNumber,
        String etag,
        long sizeBytes,
        LocalDateTime uploadedAt
) {
}
