package com.example.backend_pj4.application.query.comment;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.comment.CommentResult;
import com.example.backend_pj4.application.mapper.CommentResultMapper;
import com.example.backend_pj4.application.port.in.comment.ListCommentsQuery;
import com.example.backend_pj4.application.port.out.CommentLikeCounter;
import com.example.backend_pj4.application.port.out.CommentListCache;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.Comment;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.CommentRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.domain.repository.UserRepository;
import com.example.backend_pj4.domain.repository.filter.CommentFilter;

/**
 * Dựng cây bình luận cho trang chi tiết phim.
 *
 * <p>Số truy vấn là <b>cố định, không phụ thuộc độ sâu của cây</b>:
 *
 * <pre>
 * Cache trượt: 1 cau lay trang goc
 *              1 cau lay TOAN BO con chau moi cap  (nho cot root_id)
 *              1 cau nap tac gia theo lo
 *              1 cau lay co isLiked (bo qua neu goi khong token)
 * Cache trung: 0 cau cho cay, chi con cau isLiked
 * </pre>
 *
 * <p>Không có cột {@code root_id} thì bước hai phải dò từng cấp một — mỗi cấp một truy vấn, và
 * không biết trước khi nào dừng.
 */
@Service
public class ListCommentsQueryHandler implements ListCommentsQuery {

    private final MovieRepository movieRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CommentResultMapper commentResultMapper;
    private final CommentListCache commentListCache;
    private final CommentLikeCounter commentLikeCounter;

    public ListCommentsQueryHandler(MovieRepository movieRepository,
                                    CommentRepository commentRepository,
                                    UserRepository userRepository,
                                    CommentResultMapper commentResultMapper,
                                    CommentListCache commentListCache,
                                    CommentLikeCounter commentLikeCounter) {
        this.movieRepository = movieRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.commentResultMapper = commentResultMapper;
        this.commentListCache = commentListCache;
        this.commentLikeCounter = commentLikeCounter;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<CommentResult> execute(String movieSlug, String episodeId,
                                             PageQuery query, String viewerId) {
        Movie movie = movieRepository.findBySlug(movieSlug)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        // Ba cờ an toàn nằm ở đây chứ không phải ở controller: endpoint này là công khai, bật
        // nhầm isApproved sẽ lộ bình luận admin vừa ẩn.
        CommentFilter filter = new CommentFilter(
                movie.getId(), episodeId, true, true, Boolean.TRUE, null);

        PageResult<CommentResult> page = commentListCache.get(filter, query)
                .orElseGet(() -> {
                    PageResult<CommentResult> fresh = loadFromDatabase(filter, query);
                    commentListCache.put(filter, query, fresh);
                    return fresh;
                });

        return applyViewerState(page, viewerId);
    }

    private PageResult<CommentResult> loadFromDatabase(CommentFilter filter, PageQuery query) {
        PageResult<Comment> rootPage = commentRepository.find(filter, query);
        if (rootPage.items().isEmpty()) {
            return rootPage.withItems(List.of());
        }

        List<String> rootIds = rootPage.items().stream().map(Comment::getId).toList();

        // Loc isApproved trong bo nho chu khong o SQL: cung mot truy van con phuc vu luong xoa,
        // von can thay ca binh luan dang bi an. Con cua mot nut bi an se thanh mo coi va bien
        // mat khoi cay — dung y do "an mot nut la an ca nhanh".
        List<Comment> descendants = commentRepository.findByRootIds(rootIds).stream()
                .filter(c -> Boolean.TRUE.equals(c.getIsApproved()))
                .toList();

        // Nạp tác giả một lần cho cả cây thay vì findById trong vòng lặp: một luồng 200 trả lời
        // sẽ thành 200 truy vấn.
        Set<String> authorIds = new LinkedHashSet<>();
        rootPage.items().forEach(c -> authorIds.add(c.getUserId()));
        descendants.forEach(c -> authorIds.add(c.getUserId()));
        Map<String, User> authorsById = userRepository.findByIds(authorIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return rootPage.withItems(
                commentResultMapper.toTree(rootPage.items(), descendants, authorsById));
    }

    /**
     * Phủ phần riêng của từng người xem lên bản dùng chung.
     *
     * <p>Bản cache dùng chung cho mọi người, nên {@code likeCount} trong đó mới là ảnh chụp từ
     * cột database và {@code isLiked} còn đang tắt hết. Hai thứ đó phải cộng vào sau, không được
     * cache cùng.
     */
    private PageResult<CommentResult> applyViewerState(PageResult<CommentResult> page, String viewerId) {
        if (page.items().isEmpty()) {
            return page;
        }

        List<String> allIds = new ArrayList<>();
        collectIds(page.items(), allIds);

        Map<String, Long> deltas = commentLikeCounter.deltasOf(allIds);
        Set<String> likedIds = viewerId == null
                ? Set.of()
                : Set.copyOf(commentRepository.findLikedIdsIn(viewerId, allIds));

        return page.withItems(commentResultMapper.overlay(page.items(), deltas, likedIds));
    }

    private void collectIds(List<CommentResult> nodes, List<String> out) {
        for (CommentResult node : nodes) {
            out.add(node.id());
            collectIds(node.replies(), out);
        }
    }
}
