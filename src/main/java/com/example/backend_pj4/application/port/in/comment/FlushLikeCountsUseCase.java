package com.example.backend_pj4.application.port.in.comment;

public interface FlushLikeCountsUseCase {
    /**
     * Gộp số lượt thích đang đếm ở Redis xuống cột {@code comment.like_count}.
     *
     * @return số bình luận đã cập nhật, để job ghi log
     */
    int execute();
}
