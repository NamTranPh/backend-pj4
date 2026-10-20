package com.example.backend_pj4.application.usecase.comment;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.port.in.comment.DeleteCommentUseCase;
import com.example.backend_pj4.application.port.out.CommentListCache;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Comment;
import com.example.backend_pj4.domain.repository.CommentRepository;

/**
 * Xoá mềm một bình luận <b>và toàn bộ nhánh con</b> của nó.
 *
 * <p>Hai cách khác đều tệ hơn:
 *
 * <ul>
 *   <li><b>Để lại bia mộ "[đã xoá]"</b> — bắt phải thêm một trạng thái hiển thị chạy xuyên DTO,
 *       mapper rồi ra tới giao diện, chỉ để giữ chỗ cho một nội dung đã bị xoá.</li>
 *   <li><b>Chỉ xoá đúng nút đó</b> — nhánh con thành mồ côi nên biến mất khỏi màn hình (cây dựng
 *       từ gốc trở xuống), nhưng vẫn nằm nguyên trong database. Người dùng tưởng đã xoá hết,
 *       thực ra dữ liệu vẫn còn — rò rỉ âm thầm.</li>
 * </ul>
 */
@Service
public class DeleteCommentService implements DeleteCommentUseCase {

    private final CommentRepository commentRepository;
    private final CommentListCache commentListCache;

    public DeleteCommentService(CommentRepository commentRepository,
                                CommentListCache commentListCache) {
        this.commentRepository = commentRepository;
        this.commentListCache = commentListCache;
    }

    @Override
    @Transactional
    public void execute(String commentId, String userId, boolean isAdmin) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        // Kiểm tra ở use case chứ không chỉ dựa vào route: endpoint xoá nằm chung cho cả người
        // thường lẫn admin, chỉ nó biết bình luận này của ai (AGENTS.md §17).
        if (!isAdmin && !Objects.equals(comment.getUserId(), userId)) {
            throw new CustomException(ErrorCode.COMMENT_NOT_OWNED);
        }

        commentRepository.softDeleteByIds(collectSubtreeIds(comment));
        commentListCache.bumpVersion(comment.getMovieId());
    }

    /**
     * Gom id của chính bình luận này và mọi con cháu của nó.
     *
     * <p>Nạp cả luồng bằng <b>một</b> truy vấn nhờ cột {@code root_id}, rồi đi xuống trong bộ
     * nhớ. Không có cột đó thì phải truy vấn theo từng cấp, mỗi cấp một câu.
     */
    private Set<String> collectSubtreeIds(Comment comment) {
        String rootId = comment.getRootId() != null ? comment.getRootId() : comment.getId();
        List<Comment> thread = commentRepository.findByRootIds(List.of(rootId));

        Map<String, List<Comment>> childrenByParent = new LinkedHashMap<>();
        for (Comment node : thread) {
            childrenByParent
                    .computeIfAbsent(node.getParentId(), key -> new ArrayList<>())
                    .add(node);
        }

        Set<String> ids = new LinkedHashSet<>();
        Deque<String> stack = new ArrayDeque<>();
        stack.push(comment.getId());

        while (!stack.isEmpty()) {
            String current = stack.pop();
            // add trả false khi id đã có: vừa chống trùng vừa chặn vòng lặp vô hạn nếu dữ liệu
            // có chu trình do sửa tay trong database.
            if (!ids.add(current)) {
                continue;
            }
            for (Comment child : childrenByParent.getOrDefault(current, List.of())) {
                stack.push(child.getId());
            }
        }

        return ids;
    }
}
