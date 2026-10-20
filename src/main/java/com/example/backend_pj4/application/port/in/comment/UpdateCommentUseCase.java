package com.example.backend_pj4.application.port.in.comment;

import com.example.backend_pj4.application.dto.comment.CommentResult;

public interface UpdateCommentUseCase {
    /** Chỉ chủ sở hữu sửa được nội dung; admin không sửa hộ, chỉ ẩn hoặc xoá. */
    CommentResult execute(String commentId, String userId, String content);
}
