package com.example.backend_pj4.application.dto.comment;

import java.time.LocalDateTime;

public record MyCommentResult(
        String id,
        String content,
        String movieId,
        String movieSlug,
        String movieTitle,
        String posterUrl,
        String episodeId,
        Integer episodeNumber,
        String episodeTitle,
        String parentId,
        long likeCount,
        boolean isApproved,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
