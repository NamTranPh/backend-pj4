package com.example.backend_pj4.domain.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class UploadSession {
    private String id;
    private String targetId;
    private String targetType;
    private String rawFileKey;
    private String uploadId;
    private long fileSizeBytes;
    private String fileName;
    private String status;
    private List<UploadPart> parts;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}
