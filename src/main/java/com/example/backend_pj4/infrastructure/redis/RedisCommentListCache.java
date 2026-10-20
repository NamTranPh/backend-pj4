package com.example.backend_pj4.infrastructure.redis;

import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.example.backend_pj4.application.dto.comment.CommentResult;
import com.example.backend_pj4.application.port.out.CommentListCache;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.repository.filter.CommentFilter;
import com.example.backend_pj4.infrastructure.config.properties.CacheProperties;
import com.example.backend_pj4.infrastructure.config.properties.RedisKeyProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Cache cây bình luận bằng Redis, vô hiệu hoá theo <b>version key</b>.
 *
 * <pre>
 * {prefix}:comment:ver:v1:{movieId}                = 7
 * {prefix}:comment:list:v1:{movieId}:7:{ep}:{...}  = JSON của một trang
 *
 * có bình luận mới  --&gt;  INCR version key  --&gt;  8
 *                   --&gt;  mọi khoá gắn số 7 thành mồ côi ngay, tự chết theo TTL
 * </pre>
 *
 * <p>Cách này thay cho việc quét khoá theo mẫu rồi xoá — quét là thao tác O(n) trên toàn bộ
 * Redis và chặn server trong lúc chạy. Đổi lại được vô hiệu hoá tức thì, không có cửa sổ dữ
 * liệu cũ.
 *
 * <p>Serialize bằng JSON chứ không phải Java serialization (AGENTS.md §18).
 */
@Slf4j
@Component
public class RedisCommentListCache implements CommentListCache {

    private static final TypeReference<PageResult<CommentResult>> PAGE_TYPE = new TypeReference<>() {
    };

    private final StringRedisTemplate redisTemplate;
    private final RedisKeyProperties redisKeyProperties;
    private final CacheProperties cacheProperties;
    private final ObjectMapper objectMapper;

    public RedisCommentListCache(StringRedisTemplate redisTemplate,
                                 RedisKeyProperties redisKeyProperties,
                                 CacheProperties cacheProperties,
                                 ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.redisKeyProperties = redisKeyProperties;
        this.cacheProperties = cacheProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<PageResult<CommentResult>> get(CommentFilter filter, PageQuery query) {
        try {
            String json = redisTemplate.opsForValue().get(listKey(filter, query));
            if (json == null) {
                return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(json, PAGE_TYPE));
        } catch (Exception e) {
            // Cache chỉ là tối ưu: hỏng thì đọc thẳng database, không được làm sập luồng (§18).
            log.warn("Doc cache binh luan that bai movieId={}", filter.movieId(), e);
            return Optional.empty();
        }
    }

    @Override
    public void put(CommentFilter filter, PageQuery query, PageResult<CommentResult> value) {
        try {
            redisTemplate.opsForValue().set(
                    listKey(filter, query),
                    objectMapper.writeValueAsString(value),
                    cacheProperties.getCommentListTtl());
        } catch (Exception e) {
            log.warn("Ghi cache binh luan that bai movieId={}", filter.movieId(), e);
        }
    }

    @Override
    public void bumpVersion(String movieId) {
        if (movieId == null) {
            return;
        }
        try {
            redisTemplate.opsForValue().increment(versionKey(movieId));
        } catch (Exception e) {
            // Không tăng được version nghĩa là trang cũ còn sống tới hết TTL. Chấp nhận được hơn
            // nhiều so với để exception của Redis làm hỏng lượt đăng bình luận.
            log.warn("Tang version cache binh luan that bai movieId={}", movieId, e);
        }
    }

    private String versionKey(String movieId) {
        return redisKeyProperties.getPrefix() + ":comment:ver:v1:" + movieId;
    }

    private String currentVersion(String movieId) {
        try {
            String version = redisTemplate.opsForValue().get(versionKey(movieId));
            return version != null ? version : "0";
        } catch (Exception e) {
            log.warn("Doc version cache binh luan that bai movieId={}", movieId, e);
            return "0";
        }
    }

    /**
     * Khoá gồm đủ mọi thứ làm nội dung trang khác đi: phim, version, tập, số trang, cỡ trang và
     * cách sắp xếp. Thiếu một thành phần là hai trang khác nhau đè lên nhau.
     */
    private String listKey(CommentFilter filter, PageQuery query) {
        String episode = filter.episodeId() == null ? "_" : filter.episodeId();
        return redisKeyProperties.getPrefix() + ":comment:list:v1:"
                + filter.movieId() + ":"
                + currentVersion(filter.movieId()) + ":"
                + episode + ":"
                + query.page() + ":"
                + query.size() + ":"
                + query.sortBy() + ":"
                + query.direction();
    }
}
