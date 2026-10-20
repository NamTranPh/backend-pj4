package com.example.backend_pj4.application.dto.comment;

import java.time.LocalDateTime;

/**
 * Danh sách Comment thẳng (không có cây) - Admin
 */
public record AdminCommentResult(
        String id,
        String content,
        String movieId,
        String episodeId,
        String parentId,
        CommentAuthorResult author,
        boolean isApproved,
        long likeCount,
        LocalDateTime createdAt
) {
}
