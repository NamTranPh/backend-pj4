package com.example.backend_pj4.infrastructure.redis;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.example.backend_pj4.application.port.out.RefreshTokenStore;
import com.example.backend_pj4.domain.model.RefreshToken;
import com.example.backend_pj4.domain.repository.RefreshTokenRepository;
import com.example.backend_pj4.infrastructure.config.properties.JwtProperties;
import com.example.backend_pj4.infrastructure.config.properties.RedisKeyProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RedisRefreshTokenStore implements RefreshTokenStore {

    private final RefreshTokenRepository dbRepo;
    private final StringRedisTemplate redisTemplate;
    private final RedisKeyProperties redisKeyProperties;
    private final JwtProperties jwtProperties;

    public RedisRefreshTokenStore(RefreshTokenRepository dbRepo,
                                  StringRedisTemplate redisTemplate,
                                  RedisKeyProperties redisKeyProperties,
                                  JwtProperties jwtProperties) {
        this.dbRepo = dbRepo;
        this.redisTemplate = redisTemplate;
        this.redisKeyProperties = redisKeyProperties;
        this.jwtProperties = jwtProperties;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshToken saved = dbRepo.save(refreshToken);
        cacheToken(saved);
        return saved;
    }

    @Override
    public Optional<RefreshToken> findByTokenId(String tokenId) {
        String cached = safeGetRedis(redisKey(tokenId));
        if (cached != null) {
            return dbRepo.findByTokenId(tokenId);
        }
        Optional<RefreshToken> fromDb = dbRepo.findByTokenId(tokenId);
        fromDb.filter(t -> t.getRevokedAt() == null).ifPresent(this::cacheToken);
        return fromDb;
    }

    @Override
    public void revokeByTokenId(String tokenId) {
        dbRepo.revokeByTokenId(tokenId);
        safeDeleteRedis(redisKey(tokenId));
    }

    @Override
    public void revokeAllByUserId(String userId) {
        List<RefreshToken> active = dbRepo.findActiveByUserId(userId);
        dbRepo.revokeAllByUserId(userId);
        for (RefreshToken token : active) {
            safeDeleteRedis(redisKey(token.getTokenId()));
        }
    }

    private String redisKey(String tokenId) {
        return redisKeyProperties.getPrefix() + ":refresh:" + tokenId;
    }

    private void cacheToken(RefreshToken token) {
        try {
            Duration ttl = Duration.ofMillis(jwtProperties.getRefreshExpiration());
            redisTemplate.opsForValue().set(redisKey(token.getTokenId()), token.getUserId(), ttl);
        } catch (Exception e) {
            log.warn("Redis cache set failed for refresh token tokenId={}", token.getTokenId(), e);
        }
    }

    private String safeGetRedis(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.warn("Redis get failed for key={}", key, e);
            return null;
        }
    }

    private void safeDeleteRedis(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.warn("Redis delete failed for key={}", key, e);
        }
    }
}
