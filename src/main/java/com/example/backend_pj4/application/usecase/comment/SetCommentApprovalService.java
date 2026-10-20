package com.example.backend_pj4.application.usecase.comment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.port.in.comment.SetCommentApprovalUseCase;
import com.example.backend_pj4.application.port.out.CommentListCache;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Comment;
import com.example.backend_pj4.domain.repository.CommentRepository;

/**
 * Ẩn hoặc hiện lại một bình luận.
 *
 * <p>Ẩn một nút là ẩn <b>cả nhánh con</b> — không phải vì có vòng lặp nào đi xuống, mà vì cây
 * được dựng từ gốc trở xuống: nút bị ẩn rơi khỏi kết quả, con của nó thành mồ côi và không còn
 * đường nào hiện ra. Cố ý giữ như vậy, và đã ghi vào {@code fe_api.md}.
 */
@Service
public class SetCommentApprovalService implements SetCommentApprovalUseCase {

    private final CommentRepository commentRepository;
    private final CommentListCache commentListCache;

    public SetCommentApprovalService(CommentRepository commentRepository,
                                     CommentListCache commentListCache) {
        this.commentRepository = commentRepository;
        this.commentListCache = commentListCache;
    }

    @Override
    @Transactional
    public void execute(String commentId, boolean approved) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        commentRepository.save(comment.toBuilder().isApproved(approved).build());
        commentListCache.bumpVersion(comment.getMovieId());
    }
}
