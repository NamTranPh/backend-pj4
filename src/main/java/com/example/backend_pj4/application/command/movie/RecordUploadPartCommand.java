package com.example.backend_pj4.application.command.movie;

public record RecordUploadPartCommand(
        String sessionId,
        int partNumber,
        String etag,
        long sizeBytes
) {
}
