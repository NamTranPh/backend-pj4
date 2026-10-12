package com.example.backend_pj4.infrastructure.security;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.response.ApiResponseDto;
import com.example.backend_pj4.infrastructure.config.properties.RateLimitProperties;
import com.example.backend_pj4.infrastructure.config.properties.RedisKeyProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Giới hạn số request theo IP cho nhóm endpoint xác thực.
 *
 * <p><b>Vì sao cần, dù đã có khoá tài khoản:</b> khoá tài khoản là <em>per-account</em> — nó
 * chặn "thử 10.000 mật khẩu trên 1 tài khoản", nhưng không chặn được credential stuffing:
 * thử 1 mật khẩu phổ biến trên 10.000 tài khoản khác nhau thì không tài khoản nào chạm ngưỡng.
 * Hai lớp này bù cho nhau, không thay thế nhau.
 *
 * <p>Cũng chặn luôn việc bắn hàng loạt vào {@code /register} và {@code /forgot-password}.
 *
 * <p><b>Giới hạn đã biết:</b> IP lấy qua {@code X-Forwarded-For} nên client tự đặt được. Khi
 * chạy sau reverse proxy phải cấu hình {@code server.forward-headers-strategy} cho đúng, nếu
 * không bộ đếm dễ bị lách. Redis chết thì filter cho qua (fail-open) — lớp khoá tài khoản
 * trên database vẫn còn.
 */
@Slf4j
@Component
public class IpRateLimitFilter extends OncePerRequestFilter {

    private static final String[] PROTECTED_PREFIXES = {
            "/api/v1/auth/",
            "/api/v1/admin/auth/login"
    };

    private final StringRedisTemplate redisTemplate;
    private final RedisKeyProperties redisKeyProperties;
    private final RateLimitProperties properties;
    private final ObjectMapper objectMapper;

    public IpRateLimitFilter(StringRedisTemplate redisTemplate,
                             RedisKeyProperties redisKeyProperties,
                             RateLimitProperties properties,
                             ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.redisKeyProperties = redisKeyProperties;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!properties.isEnabled()) {
            return true;
        }
        String uri = request.getRequestURI();
        for (String prefix : PROTECTED_PREFIXES) {
            if (uri.startsWith(prefix)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if (exceededLimit(clientIp(request))) {
            writeTooManyRequests(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean exceededLimit(String ip) {
        try {
            String key = redisKeyProperties.getPrefix() + ":rate:" + ip;
            Long count = redisTemplate.opsForValue().increment(key);
            if (count != null && count == 1) {
                redisTemplate.expire(key, properties.getWindowSeconds(), TimeUnit.SECONDS);
            }
            return count != null && count > properties.getMaxRequests();
        } catch (Exception e) {
            // Redis chết thì không chặn luồng nghiệp vụ (giống RedisOtpAttemptLimiter)
            log.warn("Redis rate limit check failed for ip={}", ip, e);
            return false;
        }
    }

    private void writeTooManyRequests(HttpServletResponse response) throws IOException {
        response.setStatus(ErrorCode.TOO_MANY_REQUESTS.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader(HttpHeaders.RETRY_AFTER, String.valueOf(properties.getWindowSeconds()));
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponseDto.error(
                ErrorCode.TOO_MANY_REQUESTS.name().toLowerCase(),
                ErrorCode.TOO_MANY_REQUESTS.getMessage())));
    }

    /**
     * Ưu tiên X-Forwarded-For (phần tử đầu là client gốc) vì sau reverse proxy thì
     * getRemoteAddr() trả IP của proxy.
     */
    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            String first = forwarded.split(",")[0].trim();
            if (!first.isEmpty()) {
                return first;
            }
        }
        return request.getRemoteAddr();
    }
}
