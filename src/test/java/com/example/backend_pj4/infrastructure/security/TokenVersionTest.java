package com.example.backend_pj4.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.example.backend_pj4.infrastructure.config.properties.JwtProperties;
import com.example.backend_pj4.infrastructure.config.properties.RedisKeyProperties;
import com.example.backend_pj4.infrastructure.database.repositories.UserJpaRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Khoá hành vi của claim {@code ver} (token version).
 *
 * <p>Đổi mật khẩu vốn chỉ thu hồi refresh token; access token cũ vẫn chạy tới khi hết hạn —
 * tức là kẻ tấn công đã chiếm được access token còn nguyên 15 phút sau khi nạn nhân đổi mật khẩu.
 * Tăng {@code token_version} làm token cũ chết ngay.
 */
class TokenVersionTest {

    private static final String EMAIL = "nam@gmail.com";
    private static final String USER_PATH = "/api/v1/me/favorites";
    private static final String SECRET = "test-secret-test-secret-test-secret-test-secret-0123456789";

    private JwtTokenProvider tokenProvider;
    private JwtAuthenticationFilter filter;
    private int currentTokenVersion;

    @BeforeEach
    void setUp() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret(SECRET);
        jwtProperties.setExpiration(900_000L);
        jwtProperties.setRefreshExpiration(2_592_000_000L);
        jwtProperties.setIssuer("http://localhost:3004");
        tokenProvider = new JwtTokenProvider(jwtProperties);

        currentTokenVersion = 0;

        UserDetailsService userDetailsService = mock(UserDetailsService.class);
        lenient().when(userDetailsService.loadUserByUsername(EMAIL)).thenAnswer(inv -> new AppUserDetails(
                "usr_1", EMAIL, "{noop}x", true, true,
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                currentTokenVersion));

        StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, String> valueOps = mock(ValueOperations.class);
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOps);
        lenient().when(valueOps.setIfAbsent(anyString(), anyString(), anyLong(), any()))
                .thenReturn(Boolean.FALSE);

        RedisKeyProperties keys = new RedisKeyProperties();
        keys.setPrefix("test");

        filter = new JwtAuthenticationFilter(
                tokenProvider, userDetailsService, mock(UserJpaRepository.class), redisTemplate, keys);
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    private boolean authenticated(String token) throws Exception {
        SecurityContextHolder.clearContext();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", USER_PATH);
        request.setRequestURI(USER_PATH);
        request.addHeader("Authorization", "Bearer " + token);
        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());
        return SecurityContextHolder.getContext().getAuthentication() != null;
    }

    @Test
    void verKhop_thiDiQua() throws Exception {
        String token = tokenProvider.generateAccessToken(EMAIL, JwtTokenProvider.CHANNEL_USER, 0);

        assertThat(authenticated(token)).isTrue();
    }

    @Test
    void doiMatKhau_tokenCuChetNGAY_khongChoHet15Phut() throws Exception {
        String tokenTruocKhiDoi = tokenProvider.generateAccessToken(EMAIL, JwtTokenProvider.CHANNEL_USER, 0);
        assertThat(authenticated(tokenTruocKhiDoi)).isTrue();

        currentTokenVersion = 1; // người dùng đổi mật khẩu

        assertThat(authenticated(tokenTruocKhiDoi))
                .as("access token phát trước khi đổi mật khẩu phải bị từ chối ngay")
                .isFalse();
    }

    @Test
    void tokenPhatSauKhiDoi_vanDungDuoc() throws Exception {
        currentTokenVersion = 1;
        String tokenMoi = tokenProvider.generateAccessToken(EMAIL, JwtTokenProvider.CHANNEL_USER, 1);

        assertThat(authenticated(tokenMoi)).isTrue();
    }

    @Test
    void tokenCuKhongCoClaimVer_coiNhu0_khongPhaiBreakingChange() throws Exception {
        // Token phát trước bản nâng cấp: có typ và ch nhưng chưa có ver
        Date now = new Date();
        String tokenKhongCoVer = Jwts.builder()
                .setClaims(java.util.Map.of(
                        JwtTokenProvider.CLAIM_TOKEN_TYPE, JwtTokenProvider.TOKEN_TYPE_ACCESS,
                        JwtTokenProvider.CLAIM_CHANNEL, JwtTokenProvider.CHANNEL_USER))
                .setSubject(EMAIL)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + 900_000L))
                .setIssuer("http://localhost:3004")
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .compact();

        assertThat(authenticated(tokenKhongCoVer))
                .as("thiếu claim ver coi như 0 — người dùng chưa đổi mật khẩu vẫn dùng được")
                .isTrue();
    }
}
