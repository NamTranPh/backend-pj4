package com.example.backend_pj4.application.usecase.comment;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.port.in.comment.FlushLikeCountsUseCase;
import com.example.backend_pj4.application.port.out.CommentLikeCounter;
import com.example.backend_pj4.domain.repository.CommentRepository;

/**
 * Gộp số lượt thích đang đếm ở Redis xuống cột {@code comment.like_count}.
 *
 * <p>⚠️ Điểm quan trọng nhất của class này: nó <b>đếm lại bằng {@code COUNT(*)}</b> trên bảng
 * {@code comment_like}, <b>không</b> cộng phần chênh lệch lấy từ Redis vào cột cũ.
 *
 * <p>Vì sao: {@code comment_like} luôn là nguồn sự thật. Đếm lại thì dù Redis có restart làm mất
 * phần chênh lệch chưa gộp, lần gộp kế tiếp vẫn ra đúng số — tự chữa lành, không cần job đối
 * soát riêng. Cộng dồn thì mỗi lần mất mát là một sai lệch vĩnh viễn, và chỉ phát hiện được khi
 * có người ngồi đếm tay.
 *
 * <p>Phần chênh lệch ở Redis vì thế chỉ còn hai việc: đánh dấu bình luận nào vừa đổi, và cho con
 * số hiển thị nhúc nhích ngay lúc bấm.
 */
@Service
public class FlushLikeCountsService implements FlushLikeCountsUseCase {

    private final CommentLikeCounter commentLikeCounter;
    private final CommentRepository commentRepository;

    public FlushLikeCountsService(CommentLikeCounter commentLikeCounter,
                                  CommentRepository commentRepository) {
        this.commentLikeCounter = commentLikeCounter;
        this.commentRepository = commentRepository;
    }

    @Override
    @Transactional
    public int execute() {
        Map<String, Long> pending = commentLikeCounter.drainPendingDeltas();
        if (pending.isEmpty()) {
            return 0;
        }

        Set<String> ids = pending.keySet();
        commentRepository.recountLikeCounts(ids);
        return ids.size();
    }
}
