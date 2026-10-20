package com.example.backend_pj4.infrastructure.redis;

import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.example.backend_pj4.application.dto.rating.RatingSummaryResult;
import com.example.backend_pj4.application.port.out.RatingSummaryCache;
import com.example.backend_pj4.infrastructure.config.properties.CacheProperties;
import com.example.backend_pj4.infrastructure.config.properties.RedisKeyProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Cache điểm trung bình của phim. Mỗi phim đúng một khoá nên lúc có đánh giá mới chỉ cần xoá
 * khoá đó — không cần version key như bên bình luận, vốn có nhiều trang và nhiều kiểu sắp xếp.
 */
@Slf4j
@Component
public class RedisRatingSummaryCache implements RatingSummaryCache {

    private final StringRedisTemplate redisTemplate;
    private final RedisKeyProperties redisKeyProperties;
    private final CacheProperties cacheProperties;
    private final ObjectMapper objectMapper;

    public RedisRatingSummaryCache(StringRedisTemplate redisTemplate,
                                   RedisKeyProperties redisKeyProperties,
                                   CacheProperties cacheProperties,
                                   ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.redisKeyProperties = redisKeyProperties;
        this.cacheProperties = cacheProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<RatingSummaryResult> get(String movieId) {
        try {
            String json = redisTemplate.opsForValue().get(key(movieId));
            if (json == null) {
                return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(json, RatingSummaryResult.class));
        } catch (Exception e) {
            log.warn("Doc cache diem danh gia that bai movieId={}", movieId, e);
            return Optional.empty();
        }
    }

    @Override
    public void put(String movieId, RatingSummaryResult value) {
        try {
            redisTemplate.opsForValue().set(key(movieId),
                    objectMapper.writeValueAsString(value),
                    cacheProperties.getRatingSummaryTtl());
        } catch (Exception e) {
            log.warn("Ghi cache diem danh gia that bai movieId={}", movieId, e);
        }
    }

    @Override
    public void evict(String movieId) {
        try {
            redisTemplate.delete(key(movieId));
        } catch (Exception e) {
            log.warn("Xoa cache diem danh gia that bai movieId={}", movieId, e);
        }
    }

    private String key(String movieId) {
        return redisKeyProperties.getPrefix() + ":rating:summary:v1:" + movieId;
    }
}
