package com.example.backend_pj4.application.dto.comment;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Một nút trong cây bình luận. Con cháu nằm trong {@code replies}, lồng nhau không giới hạn cấp.
 */
public record CommentResult(
        String id,
        String content,
        String parentId,
        CommentAuthorResult author,
        long likeCount,
        boolean isLiked,  // người đang gọi API đã thích bình luận này chưa; luôn false khi gọi không token
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<CommentResult> replies // trả lời trực tiếp của nút này, đã sắp theo thời gian tăng dần
) {
}
