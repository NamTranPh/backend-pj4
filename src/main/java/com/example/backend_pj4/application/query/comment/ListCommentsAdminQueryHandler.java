package com.example.backend_pj4.application.query.comment;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.comment.AdminCommentResult;
import com.example.backend_pj4.application.mapper.CommentResultMapper;
import com.example.backend_pj4.application.port.in.comment.ListCommentsAdminQuery;
import com.example.backend_pj4.application.port.out.CommentLikeCounter;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.Comment;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.CommentRepository;
import com.example.backend_pj4.domain.repository.UserRepository;
import com.example.backend_pj4.domain.repository.filter.CommentFilter;

/**
 * Danh sách bình luận cho màn kiểm duyệt.
 *
 * <p>Cố ý <b>không cache</b>: admin lọc theo đủ kiểu tổ hợp và lượt truy cập rất thấp, nên tỉ lệ
 * trúng cache gần như bằng không — cache ở đây chỉ tốn bộ nhớ và thêm một chỗ phải nhớ xoá.
 *
 * <p>Cũng <b>không dựng cây</b>: admin cần quét nhanh nội dung vi phạm trên nhiều phim, dựng cây
 * vừa vô dụng vừa bắt nạp thêm con cháu của từng dòng.
 */
@Service
public class ListCommentsAdminQueryHandler implements ListCommentsAdminQuery {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CommentResultMapper commentResultMapper;
    private final CommentLikeCounter commentLikeCounter;

    public ListCommentsAdminQueryHandler(CommentRepository commentRepository,
                                         UserRepository userRepository,
                                         CommentResultMapper commentResultMapper,
                                         CommentLikeCounter commentLikeCounter) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.commentResultMapper = commentResultMapper;
        this.commentLikeCounter = commentLikeCounter;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<AdminCommentResult> execute(String movieId, Boolean isApproved,
                                                  String search, PageQuery query) {
        // episodeScoped = false: admin xem cả bình luận cấp phim lẫn bình luận từng tập.
        // rootOnly = false: danh sách phẳng, bình luận con cũng phải duyệt được.
        CommentFilter filter = new CommentFilter(movieId, null, false, false, isApproved, search);

        PageResult<Comment> page = commentRepository.find(filter, query);
        if (page.items().isEmpty()) {
            return page.withItems(List.of());
        }

        List<String> ids = page.items().stream().map(Comment::getId).toList();
        Map<String, Long> deltas = commentLikeCounter.deltasOf(ids);

        Map<String, User> authorsById = userRepository
                .findByIds(page.items().stream().map(Comment::getUserId).distinct().toList())
                .stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return page.withItems(page.items().stream()
                .map(c -> commentResultMapper.toAdminResult(
                        c, authorsById.get(c.getUserId()), deltas.getOrDefault(c.getId(), 0L)))
                .toList());
    }
}
