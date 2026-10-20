package com.example.backend_pj4.domain.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.Comment;
import com.example.backend_pj4.domain.repository.filter.CommentFilter;

public interface CommentRepository {

    Comment save(Comment comment);

    Optional<Comment> findById(String id);

    /** Danh sách bình luận — gốc cho màn công khai, phẳng cho màn admin, tuỳ {@link CommentFilter}. */
    PageResult<Comment> find(CommentFilter filter, PageQuery query);

    /**
     * Toàn bộ con cháu (mọi cấp) của các luồng gốc đã cho.
     *
     * <p>Đây là <b>ngoại lệ của AGENTS.md §28.1</b> — xem bảng ngoại lệ ở mục đó. Nó không phải
     * danh sách cho client xem mà là bước nạp hàng loạt của chính {@link #find} ngay phía trên:
     * {@code find} trả về một trang bình luận gốc, method này nạp hết con cháu của trang đó bằng
     * một truy vấn. Ép vào {@code find(Filter, PageQuery)} nghĩa là phân trang một thứ không có
     * khái niệm trang.
     *
     * <p>Trả về <b>cả</b> bình luận đang bị ẩn: luồng xoá cần chúng để xoá kéo theo nguyên nhánh.
     * Luồng đọc công khai tự lọc lại.
     */
    List<Comment> findByRootIds(Collection<String> rootIds);

    /** Xoá mềm hàng loạt — dùng khi xoá một bình luận kéo theo cả nhánh con của nó. */
    void softDeleteByIds(Collection<String> ids);

    /**
     * Cộng trực tiếp vào cột {@code like_count}.
     *
     * <p>Chỉ dùng làm đường lui khi Redis không ghi được (xem {@code CommentLikeCounter}). Đường
     * bình thường là đếm ở Redis rồi job flush ghi xuống bằng {@link #recountLikeCounts}.
     */
    void addLikeCount(String commentId, int delta);

    /**
     * Đếm lại {@code like_count} từ bảng {@code comment_like} cho các bình luận đã cho.
     *
     * <p>Job flush gọi method này thay vì cộng dồn delta: {@code comment_like} luôn là nguồn sự
     * thật, nên dù Redis có restart làm mất delta thì lần flush kế tiếp vẫn ra số đúng. Tự chữa
     * lành, không cần job đối soát riêng.
     */
    void recountLikeCounts(Collection<String> commentIds);

    boolean existsLike(String userId, String commentId);

    void addLike(String userId, String commentId);

    void removeLike(String userId, String commentId);

    /** Trong tập bình luận đã cho, những cái mà người này đã thích — để bật cờ {@code isLiked}. */
    List<String> findLikedIdsIn(String userId, Collection<String> commentIds);
}
