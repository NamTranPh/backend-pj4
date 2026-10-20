package com.example.backend_pj4.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.example.backend_pj4.infrastructure.config.properties.RateLimitProperties;
import com.example.backend_pj4.infrastructure.config.properties.RedisKeyProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Khoá hành vi của lớp chống bắn hàng loạt theo IP.
 *
 * <p>Lớp này bù cho khoá tài khoản chứ không thay thế: khoá tài khoản là per-account, chặn
 * "thử 10.000 mật khẩu trên 1 tài khoản" nhưng không chặn credential stuffing — thử 1 mật khẩu
 * phổ biến trên 10.000 tài khoản thì không tài khoản nào chạm ngưỡng.
 */
class IpRateLimitFilterTest {

    private static final int MAX_REQUESTS = 5;

    /**
     * Dựng giống ObjectMapper mà Spring Boot cấu hình sẵn: phải có JavaTimeModule thì mới
     * serialize được trường {@code timestamp} kiểu Instant của ApiResponseDto. ObjectMapper
     * trần sẽ ném lỗi — production không dính vì filter nhận bean của Spring.
     */
    private static ObjectMapper springLikeMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }

    private IpRateLimitFilter filter;
    private AtomicLong counter;
    private boolean redisBroken;

    @BeforeEach
    void setUp() {
        counter = new AtomicLong();
        redisBroken = false;

        StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, String> valueOps = mock(ValueOperations.class);
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOps);
        lenient().when(valueOps.increment(anyString())).thenAnswer(inv -> {
            if (redisBroken) {
                throw new IllegalStateException("Redis down");
            }
            return counter.incrementAndGet();
        });
        lenient().when(redisTemplate.expire(anyString(), anyLong(), any())).thenReturn(Boolean.TRUE);

        RedisKeyProperties redisKeyProperties = new RedisKeyProperties();
        redisKeyProperties.setPrefix("test");

        RateLimitProperties props = new RateLimitProperties();
        props.setEnabled(true);
        props.setMaxRequests(MAX_REQUESTS);
        props.setWindowSeconds(60);

        filter = new IpRateLimitFilter(redisTemplate, redisKeyProperties, props, springLikeMapper());
    }

    /** @return HTTP status sau khi chạy filter một lần */
    private int call(String uri, String ip) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", uri);
        request.setRequestURI(uri);
        request.setRemoteAddr(ip);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());
        return response.getStatus();
    }

    @Test
    void duoiNguong_choQua() throws Exception {
        for (int i = 0; i < MAX_REQUESTS; i++) {
            assertThat(call("/api/v1/auth/login", "1.2.3.4")).isEqualTo(200);
        }
    }

    @Test
    void vuotNguong_tra429() throws Exception {
        for (int i = 0; i < MAX_REQUESTS; i++) {
            call("/api/v1/auth/login", "1.2.3.4");
        }

        assertThat(call("/api/v1/auth/login", "1.2.3.4"))
                .as("request thứ %d phải bị chặn", MAX_REQUESTS + 1)
                .isEqualTo(429);
    }

    @Test
    void cuaAdminCungBiChan() throws Exception {
        for (int i = 0; i < MAX_REQUESTS; i++) {
            call("/api/v1/admin/auth/login", "1.2.3.4");
        }

        assertThat(call("/api/v1/admin/auth/login", "1.2.3.4")).isEqualTo(429);
    }

    @Test
    void duongDanNgoaiNhomXacThuc_khongDem() throws Exception {
        for (int i = 0; i < MAX_REQUESTS * 3; i++) {
            assertThat(call("/api/v1/movies", "1.2.3.4")).isEqualTo(200);
        }

        assertThat(counter.get())
                .as("chỉ nhóm endpoint xác thực mới bị đếm")
                .isZero();
    }

    @Test
    void uuTienXForwardedFor_khiChaySauProxy() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        request.setRequestURI("/api/v1/auth/login");
        request.setRemoteAddr("10.0.0.1");
        request.addHeader("X-Forwarded-For", "203.0.113.9, 10.0.0.1");

        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        assertThat(counter.get()).isEqualTo(1);
    }

    @Test
    void redisChet_choQua_khongChanNguoiDungThat() throws Exception {
        redisBroken = true;

        assertThat(call("/api/v1/auth/login", "1.2.3.4"))
                .as("Redis chết không được làm sập luồng đăng nhập — lớp khoá tài khoản trên DB vẫn còn")
                .isEqualTo(200);
    }

    @Test
    void tatCauHinh_khongChanGiCa() throws Exception {
        RateLimitProperties off = new RateLimitProperties();
        off.setEnabled(false);
        off.setMaxRequests(1);
        off.setWindowSeconds(60);

        StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
        RedisKeyProperties keys = new RedisKeyProperties();
        keys.setPrefix("test");
        IpRateLimitFilter disabled = new IpRateLimitFilter(redisTemplate, keys, off, springLikeMapper());

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        request.setRequestURI("/api/v1/auth/login");
        MockHttpServletResponse response = new MockHttpServletResponse();

        for (int i = 0; i < 10; i++) {
            disabled.doFilter(request, response, new MockFilterChain());
        }

        assertThat(response.getStatus()).isEqualTo(200);
    }
}
