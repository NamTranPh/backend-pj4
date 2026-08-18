package com.example.backend_pj4.application.command.movie;

public record StartUploadCommand(
        String targetId,
        String targetType,
        String fileName,
        long fileSizeBytes
) {
}
