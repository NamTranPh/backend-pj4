package com.example.backend_pj4.application.usecase.auth;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.port.in.auth.RevokeOtherSessionsUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.RefreshToken;
import com.example.backend_pj4.domain.repository.RefreshTokenRepository;
import com.example.backend_pj4.infrastructure.config.properties.RedisKeyProperties;
import com.example.backend_pj4.infrastructure.security.JwtTokenProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RevokeOtherSessionsService implements RevokeOtherSessionsUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;
    private final RedisKeyProperties redisKeyProperties;

    @Autowired
    public RevokeOtherSessionsService(RefreshTokenRepository refreshTokenRepository,
                                     JwtTokenProvider jwtTokenProvider,
                                     StringRedisTemplate redisTemplate,
                                     RedisKeyProperties redisKeyProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
        this.redisKeyProperties = redisKeyProperties;
    }

    /** Constructor hỗ trợ unit test. */
    public RevokeOtherSessionsService(RefreshTokenRepository refreshTokenRepository,
                                     JwtTokenProvider jwtTokenProvider) {
        this(refreshTokenRepository, jwtTokenProvider, null, null);
    }

    @Override
    @Transactional
    public void execute(String userId, String rawRefreshToken) {
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID_OR_EXPIRED);
        }

        String tokenId;
        try {
            tokenId = jwtTokenProvider.getTokenIdFromToken(rawRefreshToken);
        } catch (Exception e) {
            log.warn("Không thể giải mã tokenId từ refresh token: {}", e.getMessage());
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID_OR_EXPIRED);
        }

        if (tokenId == null || tokenId.isBlank()) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID_OR_EXPIRED);
        }

        RefreshToken currentToken = refreshTokenRepository.findByTokenId(tokenId)
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_INVALID_OR_EXPIRED));

        if (!userId.equals(currentToken.getUserId())) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID_OR_EXPIRED);
        }

        if (currentToken.getRevokedAt() != null
                || (currentToken.getExpiresAt() != null && currentToken.getExpiresAt().isBefore(LocalDateTime.now()))) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID_OR_EXPIRED);
        }

        String currentSessionId = currentToken.getSessionId();
        if (currentSessionId == null || currentSessionId.isBlank()) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID_OR_EXPIRED);
        }

        // Lấy danh sách các sessionId khác trước khi revoke để đưa vào Redis Blacklist
        List<String> otherSessionIds = null;
        try {
            otherSessionIds = refreshTokenRepository.findActiveSessionIdsByUserIdExceptSession(userId, currentSessionId);
        } catch (Exception e) {
            log.warn("Không thể lấy danh sách sessionId khác: {}", e.getMessage());
        }

        refreshTokenRepository.revokeAllByUserIdExceptSession(userId, currentSessionId);

        // Đưa các sessionId bị thu hồi vào Redis Blacklist trong 15 phút
        try {
            if (redisTemplate != null && redisKeyProperties != null && otherSessionIds != null) {
                for (String sid : otherSessionIds) {
                    if (sid != null && !sid.isBlank()) {
                        String key = redisKeyProperties.getPrefix() + ":revoked:session:" + sid;
                        redisTemplate.opsForValue().set(key, "1", 15, TimeUnit.MINUTES);
                    }
                }
                log.debug("Đã thêm {} phiên vào Redis blacklist 15 phút", otherSessionIds.size());
            }
        } catch (Exception e) {
            log.warn("Không thể ghi danh sách session blacklist vào Redis: {}", e.getMessage());
        }

        log.info("Đã đăng xuất tất cả các thiết bị khác của userId={}, giữ lại sessionId={}", userId, currentSessionId);
    }
}
