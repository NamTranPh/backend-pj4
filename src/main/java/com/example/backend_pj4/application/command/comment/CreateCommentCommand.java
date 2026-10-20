package com.example.backend_pj4.application.command.comment;

/**
 * @param episodeId null nghĩa là bình luận ở cấp phim. Khi có {@code parentId}, giá trị này là null
 * @param parentId  null nghĩa là tạo bình luận gốc
 */
public record CreateCommentCommand(
        String userId,
        String movieSlug,
        String episodeId,
        String parentId,
        String content
) {
}
