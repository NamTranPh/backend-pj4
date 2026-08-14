package com.example.backend_pj4.infrastructure.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.example.backend_pj4.application.port.out.LoginLockManager;
import com.example.backend_pj4.infrastructure.config.properties.LoginLockProperties;
import com.example.backend_pj4.infrastructure.config.properties.RedisKeyProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RedisLoginLockManager implements LoginLockManager {

    private final StringRedisTemplate redisTemplate;
    private final RedisKeyProperties redisKeyProperties;
    private final LoginLockProperties lockProps;

    public RedisLoginLockManager(StringRedisTemplate redisTemplate,
                                  RedisKeyProperties redisKeyProperties,
                                  LoginLockProperties lockProps) {
        this.redisTemplate = redisTemplate;
        this.redisKeyProperties = redisKeyProperties;
        this.lockProps = lockProps;
    }

    @Override
    public boolean isLocked(String email) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(lockKey(email)));
        } catch (Exception e) {
            log.warn("Redis isLocked check failed for email={}", email, e);
            return false;
        }
    }

    @Override
    public void recordFailure(String email) {
        try {
            String failKey = failKey(email);
            Long count = redisTemplate.opsForValue().increment(failKey);
            if (count != null && count == 1) {
                redisTemplate.expire(failKey, lockProps.getFailureWindowMinutes(), TimeUnit.MINUTES);
            }
            if (count != null && count >= lockProps.getMaxFailures()) {
                redisTemplate.opsForValue().set(lockKey(email), "1", lockProps.getLockMinutes(), TimeUnit.MINUTES);
                redisTemplate.delete(failKey);
            }
        } catch (Exception e) {
            log.warn("Redis recordFailure failed for email={}", email, e);
        }
    }

    @Override
    public void resetFailures(String email) {
        try {
            redisTemplate.delete(failKey(email));
            redisTemplate.delete(lockKey(email));
        } catch (Exception e) {
            log.warn("Redis resetFailures failed for email={}", email, e);
        }
    }

    private String failKey(String email) {
        return redisKeyProperties.getPrefix() + ":login-fail:" + email;
    }

    private String lockKey(String email) {
        return redisKeyProperties.getPrefix() + ":login-lock:" + email;
    }
}
