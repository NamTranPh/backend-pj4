package com.example.backend_pj4.infrastructure.redis;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.example.backend_pj4.application.port.out.CommentLikeCounter;
import com.example.backend_pj4.infrastructure.config.properties.RedisKeyProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * Đếm lượt thích ở Redis để lượt bấm không phải {@code UPDATE} vào cùng một dòng
 * {@code comment} mỗi lần — một bình luận đang nóng sẽ khiến các lượt thích xếp hàng trên khoá
 * dòng của nó.
 *
 * <pre>
 * {prefix}:like:delta:v1:{commentId}  = 3      phần chênh lệch chưa gộp xuống database
 * {prefix}:like:dirty:v1              = SET    những bình luận đang có chênh lệch
 * </pre>
 *
 * <p>Giữ thêm tập {@code dirty} thay vì quét khoá theo mẫu lúc gộp: quét là thao tác O(n) trên
 * toàn bộ Redis.
 *
 * <p>Đây <b>không</b> phải nguồn sự thật — bảng {@code comment_like} mới là. Nhờ vậy mất sạch
 * Redis cũng chỉ làm con số hiển thị đứng yên tới lần gộp kế tiếp, lúc đó job đếm lại bằng
 * {@code COUNT(*)} và mọi thứ tự khớp lại.
 */
@Slf4j
@Component
public class RedisCommentLikeCounter implements CommentLikeCounter {

    private final StringRedisTemplate redisTemplate;
    private final RedisKeyProperties redisKeyProperties;

    public RedisCommentLikeCounter(StringRedisTemplate redisTemplate,
                                   RedisKeyProperties redisKeyProperties) {
        this.redisTemplate = redisTemplate;
        this.redisKeyProperties = redisKeyProperties;
    }

    @Override
    public boolean tryIncrement(String commentId, int delta) {
        try {
            redisTemplate.opsForValue().increment(deltaKey(commentId), delta);
            redisTemplate.opsForSet().add(dirtyKey(), commentId);
            return true;
        } catch (Exception e) {
            // Trả false chứ không nuốt lỗi: caller phải cộng thẳng vào database, nếu không con
            // số sẽ đứng yên ngay trước mắt người vừa bấm.
            log.warn("Tang bo dem like tren Redis that bai commentId={}", commentId, e);
            return false;
        }
    }

    @Override
    public Map<String, Long> deltasOf(Collection<String> commentIds) {
        if (commentIds == null || commentIds.isEmpty()) {
            return Map.of();
        }
        try {
            List<String> keys = commentIds.stream().map(this::deltaKey).toList();
            List<String> values = redisTemplate.opsForValue().multiGet(keys);
            if (values == null) {
                return Map.of();
            }

            Map<String, Long> out = new HashMap<>();
            int i = 0;
            for (String commentId : commentIds) {
                String raw = i < values.size() ? values.get(i) : null;
                i++;
                if (raw != null) {
                    out.put(commentId, Long.parseLong(raw));
                }
            }
            return out;
        } catch (Exception e) {
            // Không đọc được phần chênh lệch thì hiển thị con số trong database — cũ hơn một
            // chút chứ không sai, và không làm hỏng việc xem bình luận.
            log.warn("Doc bo dem like tren Redis that bai", e);
            return Map.of();
        }
    }

    @Override
    public Map<String, Long> drainPendingDeltas() {
        try {
            Set<String> dirty = redisTemplate.opsForSet().members(dirtyKey());
            if (dirty == null || dirty.isEmpty()) {
                return Map.of();
            }

            Map<String, Long> out = new HashMap<>();
            for (String commentId : dirty) {
                // getAndDelete là nguyên tử: lượt thích xen vào ngay sau đó mở một khoản chênh
                // lệch mới chứ không bị nuốt mất.
                String raw = redisTemplate.opsForValue().getAndDelete(deltaKey(commentId));
                redisTemplate.opsForSet().remove(dirtyKey(), commentId);
                out.put(commentId, raw == null ? 0L : Long.parseLong(raw));
            }
            return out;
        } catch (Exception e) {
            log.warn("Gom bo dem like tu Redis that bai", e);
            return Map.of();
        }
    }

    private String deltaKey(String commentId) {
        return redisKeyProperties.getPrefix() + ":like:delta:v1:" + commentId;
    }

    private String dirtyKey() {
        return redisKeyProperties.getPrefix() + ":like:dirty:v1";
    }
}
