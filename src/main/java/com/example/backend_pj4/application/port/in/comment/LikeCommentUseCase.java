package com.example.backend_pj4.application.port.in.comment;

public interface LikeCommentUseCase {
    /**
     * Một cổng cho cả thích và bỏ thích: hai chiều dùng chung toàn bộ kiểm tra đầu vào, tách
     * thành hai file chỉ để đổi dấu cộng trừ là thừa.
     *
     * @param like true là thích, false là bỏ thích
     */
    void execute(String commentId, String userId, boolean like);
}
