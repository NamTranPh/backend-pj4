package com.example.backend_pj4.domain.repository.filter;

/**
 * Filter phục vụ cả hai màn hình: danh sách công khai dưới phim
 * và màn kiểm duyệt của admin.
 * @param movieId       null nghĩa là mọi phim (màn admin)
 * @param episodeId     chỉ có nghĩa khi {@code episodeScoped} là true; null lúc đó nghĩa là
 *                      "bình luận cấp phim", tức {@code episode_id IS NULL}
 * @param episodeScoped true thì lọc đúng theo {@code episodeId}; false thì không đụng tới cột
 *                      {@code episode_id} (màn admin xem cả bình luận phim lẫn bình luận tập)
 * @param rootOnly      true thì chỉ lấy bình luận gốc ({@code parent_id IS NULL}) — danh sách
 *                      công khai phân trang theo gốc; false thì lấy phẳng (màn admin)
 * @param isApproved    null nghĩa là không lọc theo trạng thái duyệt
 */
public record CommentFilter(
        String movieId,
        String episodeId,
        boolean episodeScoped,
        boolean rootOnly,
        Boolean isApproved,
        String q,
        String userId
) {
    public CommentFilter(String movieId, String episodeId, boolean episodeScoped, boolean rootOnly, Boolean isApproved, String q) {
        this(movieId, episodeId, episodeScoped, rootOnly, isApproved, q, null);
    }
}
