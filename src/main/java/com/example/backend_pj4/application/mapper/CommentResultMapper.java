package com.example.backend_pj4.application.mapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.application.dto.comment.AdminCommentResult;
import com.example.backend_pj4.application.dto.comment.CommentAuthorResult;
import com.example.backend_pj4.application.dto.comment.CommentResult;
import com.example.backend_pj4.application.service.StorageUrlResolver;
import com.example.backend_pj4.domain.model.Comment;
import com.example.backend_pj4.domain.model.User;

/**
 * Dựng cây bình luận và phủ thông tin riêng của từng người xem lên trên.
 *
 * <p>Việc map chia làm hai bước có chủ đích, vì bản cache dùng chung cho mọi người xem:
 *
 * <ol>
 *   <li>{@link #toTree} dựng cây với {@code likeCount} lấy nguyên từ cột database và
 *       {@code isLiked} luôn false. Đây là thứ được cache.</li>
 *   <li>{@link #overlay} cộng phần chênh lệch đang nằm ở Redis và bật cờ {@code isLiked} của
 *       đúng người đang gọi. Chạy sau mỗi lần lấy ra, dù từ cache hay từ database.</li>
 * </ol>
 *
 * <p>Gộp hai bước làm một là cache con số đã cộng delta, lần sau cộng tiếp thành đếm hai lần.
 *
 * <p>Là {@code @Component} chứ không phải utility tĩnh vì cần {@link StorageUrlResolver} để đổi
 * objectKey ảnh đại diện thành URL (AGENTS.md §31.5).
 */
@Component
public class CommentResultMapper {

    private final StorageUrlResolver storageUrlResolver;

    public CommentResultMapper(StorageUrlResolver storageUrlResolver) {
        this.storageUrlResolver = storageUrlResolver;
    }

    /**
     * Ghép danh sách phẳng thành cây.
     *
     * @param roots       bình luận gốc của trang hiện tại, đã sắp theo yêu cầu của client
     * @param descendants toàn bộ con cháu mọi cấp của các gốc đó, sắp theo thời gian tăng dần
     * @param authorsById tác giả đã nạp sẵn theo lô; thiếu ai thì chỗ đó chỉ còn id
     */
    public List<CommentResult> toTree(List<Comment> roots,
                                      List<Comment> descendants,
                                      Map<String, User> authorsById) {
        // Gom con theo cha trước, rồi mới đệ quy xuống: nếu dò cả danh sách cho từng nút thì
        // một luồng 1000 trả lời thành một triệu lần so sánh.
        Map<String, List<Comment>> childrenByParent = new LinkedHashMap<>();
        for (Comment child : descendants) {
            childrenByParent
                    .computeIfAbsent(child.getParentId(), key -> new ArrayList<>())
                    .add(child);
        }

        List<CommentResult> out = new ArrayList<>(roots.size());
        for (Comment root : roots) {
            out.add(buildNode(root, childrenByParent, authorsById));
        }
        return out;
    }

    /**
     * Phủ phần chênh lệch lượt thích và cờ đã thích của người đang xem lên cây.
     *
     * @param likeDeltas phần chênh lệch chưa gộp xuống database, theo id bình luận
     * @param likedIds   những bình luận người đang xem đã thích; rỗng khi gọi không token
     */
    public List<CommentResult> overlay(List<CommentResult> nodes,
                                       Map<String, Long> likeDeltas,
                                       Set<String> likedIds) {
        if (nodes == null || nodes.isEmpty()) {
            return List.of();
        }

        List<CommentResult> out = new ArrayList<>(nodes.size());
        for (CommentResult node : nodes) {
            long delta = likeDeltas.getOrDefault(node.id(), 0L);
            out.add(new CommentResult(
                    node.id(),
                    node.content(),
                    node.parentId(),
                    node.author(),
                    // Không để con số âm: cột like_count vừa được gộp xong trong khi phần chênh
                    // lệch âm chưa kịp xoá sẽ cho ra -1, trông như lỗi.
                    Math.max(0, node.likeCount() + delta),
                    likedIds.contains(node.id()),
                    node.createdAt(),
                    node.updatedAt(),
                    overlay(node.replies(), likeDeltas, likedIds)));
        }
        return out;
    }

    public AdminCommentResult toAdminResult(Comment comment, User author, long likeDelta) {
        return new AdminCommentResult(
                comment.getId(),
                comment.getContent(),
                comment.getMovieId(),
                comment.getEpisodeId(),
                comment.getParentId(),
                toAuthor(comment.getUserId(), author),
                Boolean.TRUE.equals(comment.getIsApproved()),
                Math.max(0, safeLikeCount(comment) + likeDelta),
                comment.getCreatedAt());
    }

    /** Một nút rời, dùng cho phản hồi ngay sau khi tạo hoặc sửa bình luận. */
    public CommentResult toResult(Comment comment, User author) {
        return new CommentResult(
                comment.getId(),
                comment.getContent(),
                comment.getParentId(),
                toAuthor(comment.getUserId(), author),
                safeLikeCount(comment),
                false,
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                List.of());
    }

    public CommentAuthorResult toAuthor(String userId, User author) {
        if (author == null) {
            // Tác giả đã bị xoá: vẫn phải trả về một nút hợp lệ, bỏ qua sẽ làm đứt nhánh con.
            return new CommentAuthorResult(userId, null, null);
        }
        return new CommentAuthorResult(
                author.getId(),
                author.getName(),
                storageUrlResolver.resolveAvatar(author.getProfileUrl()));
    }

    private CommentResult buildNode(Comment comment,
                                    Map<String, List<Comment>> childrenByParent,
                                    Map<String, User> authorsById) {
        List<Comment> children = childrenByParent.getOrDefault(comment.getId(), List.of());
        List<CommentResult> replies = new ArrayList<>(children.size());
        for (Comment child : children) {
            replies.add(buildNode(child, childrenByParent, authorsById));
        }

        return new CommentResult(
                comment.getId(),
                comment.getContent(),
                comment.getParentId(),
                toAuthor(comment.getUserId(), authorsById.get(comment.getUserId())),
                safeLikeCount(comment),
                false,
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                replies);
    }

    private long safeLikeCount(Comment comment) {
        return comment.getLikeCount() == null ? 0L : comment.getLikeCount();
    }
}
