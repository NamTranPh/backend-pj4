package com.example.backend_pj4.application.port.out;

import java.util.Collection;
import java.util.Map;

/**
 * Bộ đếm lượt thích đặt ở Redis, để lượt thích không phải {@code UPDATE} vào cùng một dòng
 * {@code comment} mỗi lần bấm — một bình luận đang nóng sẽ khiến các lượt thích xếp hàng trên
 * khoá dòng.
 *
 * <p>Đây <b>không</b> phải nguồn sự thật: bảng {@code comment_like} mới là. Redis chỉ giữ phần
 * chênh lệch chưa gộp xuống cột {@code comment.like_count}.
 */
public interface CommentLikeCounter {

    /**
     * @return false khi Redis không ghi được — caller phải tự cộng thẳng vào database để con số
     *         không đứng yên trước mắt người vừa bấm
     */
    boolean tryIncrement(String commentId, int delta);

    /** Phần chênh lệch chưa gộp, để cộng vào {@code like_count} lúc hiển thị. */
    Map<String, Long> deltasOf(Collection<String> commentIds);

    /**
     * Lấy ra và xoá sạch phần chênh lệch đang chờ, cho job gộp định kỳ.
     *
     * <p>Phải đọc-và-xoá nguyên tử: lượt thích xen vào ngay sau đó sẽ mở một khoản chênh lệch
     * mới chứ không bị nuốt mất.
     */
    Map<String, Long> drainPendingDeltas();
}
