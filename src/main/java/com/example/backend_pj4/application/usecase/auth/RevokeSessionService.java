package com.example.backend_pj4.application.usecase.auth;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.port.in.auth.RevokeSessionUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.repository.RefreshTokenRepository;
import com.example.backend_pj4.infrastructure.config.properties.RedisKeyProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RevokeSessionService implements RevokeSessionUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final StringRedisTemplate redisTemplate;
    private final RedisKeyProperties redisKeyProperties;

    @Autowired
    public RevokeSessionService(RefreshTokenRepository refreshTokenRepository,
                                StringRedisTemplate redisTemplate,
                                RedisKeyProperties redisKeyProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.redisTemplate = redisTemplate;
        this.redisKeyProperties = redisKeyProperties;
    }

    /** Constructor hỗ trợ unit test. */
    public RevokeSessionService(RefreshTokenRepository refreshTokenRepository) {
        this(refreshTokenRepository, null, null);
    }

    @Override
    @Transactional
    public void execute(String userId, String sessionId) {
        // Chỉ cho thu hồi phiên thuộc về chính người gọi, nếu không thì biết sessionId của người
        // khác là đăng xuất được họ. Phiên không tồn tại và phiên của người khác trả cùng một lỗi
        // để không tiết lộ sessionId nào có thật.
        if (!refreshTokenRepository.existsActiveSession(userId, sessionId)) {
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        refreshTokenRepository.revokeAllBySessionId(sessionId);

        // Đưa sessionId vào Redis Blacklist trong 15 phút (vòng đời tối đa của access token)
        try {
            if (redisTemplate != null && redisKeyProperties != null && sessionId != null) {
                String key = redisKeyProperties.getPrefix() + ":revoked:session:" + sessionId;
                redisTemplate.opsForValue().set(key, "1", 15, TimeUnit.MINUTES);
                log.debug("Đã thêm sessionId={} vào Redis blacklist 15 phút", sessionId);
            }
        } catch (Exception e) {
            log.warn("Không thể ghi session blacklist vào Redis (DB đã cập nhật an toàn): {}", e.getMessage());
        }

        log.info("Đã đăng xuất phiên sessionId={} của userId={}", sessionId, userId);
    }
}
