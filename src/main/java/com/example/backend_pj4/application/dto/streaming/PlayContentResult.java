package com.example.backend_pj4.application.dto.streaming;

public record PlayContentResult(
        String contentId,
        String contentType,
        String playbackUrl,
        String playbackToken,
        Integer resumePositionSeconds
) {
}
