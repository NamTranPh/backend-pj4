package com.example.backend_pj4.domain.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class UploadPart {
    private String id;
    private int partNumber;
    private String etag;
    private long sizeBytes;
    private LocalDateTime uploadedAt;
}
