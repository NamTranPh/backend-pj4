package com.example.backend_pj4.infrastructure.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.example.backend_pj4.application.port.out.OtpAttemptLimiter;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.OtpType;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.infrastructure.config.properties.OtpProperties;
import com.example.backend_pj4.infrastructure.config.properties.RedisKeyProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * Đếm số lần thử OTP bằng Redis. Key hết hạn đúng bằng TTL của OTP nên tự dọn rác,
 * không cần thêm cột trong database.
 */
@Slf4j
@Component
public class RedisOtpAttemptLimiter implements OtpAttemptLimiter {

    private final StringRedisTemplate redisTemplate;
    private final RedisKeyProperties redisKeyProperties;
    private final OtpProperties otpProperties;

    public RedisOtpAttemptLimiter(StringRedisTemplate redisTemplate,
                                  RedisKeyProperties redisKeyProperties,
                                  OtpProperties otpProperties) {
        this.redisTemplate = redisTemplate;
        this.redisKeyProperties = redisKeyProperties;
        this.otpProperties = otpProperties;
    }

    @Override
    public void checkAndIncrement(String email, OtpType type) {
        Long count;
        try {
            String key = attemptKey(email, type);
            count = redisTemplate.opsForValue().increment(key);
            if (count != null && count == 1) {
                redisTemplate.expire(key, otpProperties.getTtlMinutes(), TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            // Redis chết thì không chặn luồng nghiệp vụ (giống RedisLoginLockManager).
            log.warn("Redis OTP attempt check failed for email={} type={}", email, type, e);
            return;
        }

        if (count != null && count > otpProperties.getMaxAttempts()) {
            throw new CustomException(ErrorCode.OTP_MAX_ATTEMPTS_EXCEEDED);
        }
    }

    @Override
    public void reset(String email, OtpType type) {
        try {
            redisTemplate.delete(attemptKey(email, type));
        } catch (Exception e) {
            log.warn("Redis OTP attempt reset failed for email={} type={}", email, type, e);
        }
    }

    private String attemptKey(String email, OtpType type) {
        return redisKeyProperties.getPrefix() + ":otp-attempt:" + type.name() + ":" + email;
    }
}
