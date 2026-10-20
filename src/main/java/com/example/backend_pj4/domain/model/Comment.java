package com.example.backend_pj4.domain.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class Comment {
    private String id;
    private String userId;
    private String movieId;
    private String episodeId; 
    private String content;
    private String parentId;
    private String rootId; /** Gốc của cả luồng; */
    private Boolean isApproved;
    /**
     * Số lượt thích tại lần flush gần nhất, <b>không</b> phải số thời gian thực.
     *
     * <p>Nguồn sự thật là bảng {@code comment_like}. Số hiển thị cho client = cột này cộng với
     * phần delta đang nằm ở Redis (xem {@code CommentLikeCounter}).
     */
    private Integer likeCount; 
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Tính {@code rootId} cho một bình luận sắp tạo.
     *
     * <p>Chỗ này sai một lần là cây vỡ vĩnh viễn: trả lời một bình luận <b>cháu</b> thì phải lấy
     * {@code rootId} của cha, <b>không phải</b> {@code id} của cha — lấy nhầm thì nhánh đó tách
     * khỏi gốc và biến mất khỏi danh sách.
     *
     * @param parent bình luận đang được trả lời; null nghĩa là đang tạo bình luận gốc
     */
    public static String resolveRootId(Comment parent) {
        if (parent == null) {
            return null;
        }
        return parent.getRootId() != null ? parent.getRootId() : parent.getId();
    }
}
