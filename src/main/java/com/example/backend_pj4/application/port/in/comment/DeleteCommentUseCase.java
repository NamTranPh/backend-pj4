package com.example.backend_pj4.application.port.in.comment;

public interface DeleteCommentUseCase {
    /**
     * Xoá mềm bình luận <b>và toàn bộ nhánh con</b> của nó.
     *
     * @param isAdmin admin xoá được bình luận của người khác; người thường chỉ xoá của mình
     */
    void execute(String commentId, String userId, boolean isAdmin);
}
