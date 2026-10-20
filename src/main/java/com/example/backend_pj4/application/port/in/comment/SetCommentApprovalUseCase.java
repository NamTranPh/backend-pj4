package com.example.backend_pj4.application.port.in.comment;

public interface SetCommentApprovalUseCase {
    /**
     * Ẩn hoặc hiện lại một bình luận.
     *
     * <p>Ẩn một nút là ẩn <b>cả nhánh con</b> của nó: truy vấn gốc và truy vấn con cháu đều lọc
     * {@code isApproved = true}, nên con của một nút bị ẩn không còn đường nào hiện ra.
     */
    void execute(String commentId, boolean approved);
}
