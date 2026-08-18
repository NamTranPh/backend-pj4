package com.example.backend_pj4.application.dto.movie;

public record PresignedUrlResult(
        int partNumber,
        String uploadUrl
) {
}
