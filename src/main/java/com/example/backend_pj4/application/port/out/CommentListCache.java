package com.example.backend_pj4.application.port.out;

import java.util.Optional;

import com.example.backend_pj4.application.dto.comment.CommentResult;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.repository.filter.CommentFilter;

/**
 * Cache cây bình luận công khai. Cache-aside: đọc trượt thì nạp từ database rồi ghi lại vào đây.
 *
 * <p>Giá trị cache <b>dùng chung cho mọi người xem</b>, kể cả khách chưa đăng nhập — nên nó
 * không mang theo cờ {@code isLiked} đúng của ai cả, và {@code likeCount} trong đó chỉ là ảnh
 * chụp từ cột database. Query handler phủ hai thứ đó lên sau khi lấy ra.
 *
 * <p>Hỏng cache <b>không được</b> làm hỏng nghiệp vụ (AGENTS.md §18): mọi cài đặt phải nuốt lỗi
 * Redis, ghi log cảnh báo, rồi để luồng rơi về database.
 */
public interface CommentListCache {

    Optional<PageResult<CommentResult>> get(CommentFilter filter, PageQuery query);

    void put(CommentFilter filter, PageQuery query, PageResult<CommentResult> value);

    /**
     * Vô hiệu hoá toàn bộ cache bình luận của một phim — mọi trang, mọi kiểu sắp xếp, mọi tập.
     *
     * <p>Cài đặt bằng cách tăng một số version nhúng trong khoá cache, <b>không</b> quét khoá:
     * {@code KEYS}/{@code SCAN} theo mẫu là thao tác O(n) trên toàn bộ Redis. Tăng version thì
     * mọi khoá cũ thành mồ côi ngay lập tức và tự hết hạn theo TTL.
     */
    void bumpVersion(String movieId);
}
