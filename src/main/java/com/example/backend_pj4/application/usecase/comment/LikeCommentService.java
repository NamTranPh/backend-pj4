package com.example.backend_pj4.application.usecase.comment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.port.in.comment.LikeCommentUseCase;
import com.example.backend_pj4.application.port.out.CommentLikeCounter;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Comment;
import com.example.backend_pj4.domain.repository.CommentRepository;

/**
 * Thích và bỏ thích một bình luận.
 *
 * <p>Chia việc giữa hai kho một cách có chủ đích:
 *
 * <ul>
 *   <li><b>Bảng {@code comment_like} — ghi thẳng MySQL.</b> Đây là nguồn sự thật. Nó trả lời
 *       được "ai đã thích cái gì", nhờ vậy tải lại trang vẫn thấy tim đỏ, và
 *       {@code UNIQUE(user_id, comment_id)} chặn được bấm thích hai lần. Con số đơn thuần không
 *       làm được việc nào trong hai việc đó.</li>
 *   <li><b>Con số đếm — Redis.</b> Nếu mỗi lượt thích đều {@code UPDATE comment SET like_count}
 *       thì một bình luận đang nóng sẽ bắt mọi người xếp hàng trên khoá dòng của nó. Phần chênh
 *       lệch nằm ở Redis, job gộp xuống sau.</li>
 * </ul>
 *
 * <p>Redis hỏng cũng không mất dữ liệu: lượt thích đã nằm trong {@code comment_like}, chỉ con số
 * hiển thị là phải cộng thẳng vào database như cách cũ.
 */
@Service
public class LikeCommentService implements LikeCommentUseCase {

    private final CommentRepository commentRepository;
    private final CommentLikeCounter commentLikeCounter;

    public LikeCommentService(CommentRepository commentRepository,
                              CommentLikeCounter commentLikeCounter) {
        this.commentRepository = commentRepository;
        this.commentLikeCounter = commentLikeCounter;
    }

    @Override
    @Transactional
    public void execute(String commentId, String userId, boolean like) {
        // findById đã lọc bình luận bị xoá mềm, nên thích một bình luận đã xoá sẽ dừng ở đây.
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        boolean alreadyLiked = commentRepository.existsLike(userId, comment.getId());

        if (like) {
            if (alreadyLiked) {
                throw new CustomException(ErrorCode.COMMENT_ALREADY_LIKED);
            }
            commentRepository.addLike(userId, comment.getId());
        } else {
            if (!alreadyLiked) {
                throw new CustomException(ErrorCode.COMMENT_LIKE_NOT_FOUND);
            }
            commentRepository.removeLike(userId, comment.getId());
        }

        int delta = like ? 1 : -1;
        if (!commentLikeCounter.tryIncrement(comment.getId(), delta)) {
            // Redis không ghi được: cộng thẳng vào database. Chậm hơn, nhưng con số không được
            // phép đứng yên ngay trước mắt người vừa bấm.
            commentRepository.addLikeCount(comment.getId(), delta);
        }
    }
}
