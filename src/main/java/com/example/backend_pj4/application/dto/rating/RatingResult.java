package com.example.backend_pj4.application.dto.rating;

import java.time.LocalDateTime;

import com.example.backend_pj4.application.dto.comment.CommentAuthorResult;

public record RatingResult(
        String id,
        String movieId,
        int score,
        String review,
        CommentAuthorResult author,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
