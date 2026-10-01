package com.example.backend_pj4.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.example.backend_pj4.infrastructure.config.properties.JwtProperties;
import com.example.backend_pj4.infrastructure.config.properties.RedisKeyProperties;
import com.example.backend_pj4.infrastructure.database.repositories.UserJpaRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Kiểm chứng hai lớp lọc mới của JwtAuthenticationFilter:
 * 1. Chỉ access token (claim typ=access) mới xác thực được request.
 * 2. Token phát ở cửa user (ch=user) không đi qua được /api/v1/admin/**.
 */
class JwtChannelIsolationTest {

    private static final String EMAIL = "admin@admin.vn";
    private static final String ADMIN_PATH = "/api/v1/admin/movies";
    private static final String USER_PATH = "/api/v1/me/favorites";

    private static final String SECRET = "test-secret-test-secret-test-secret-test-secret-0123456789";

    private JwtTokenProvider tokenProvider;
    private JwtAuthenticationFilter filter;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret(SECRET);
        jwtProperties.setExpiration(900_000L);
        jwtProperties.setRefreshExpiration(2_592_000_000L);
        jwtProperties.setIssuer("http://localhost:3004");
        tokenProvider = new JwtTokenProvider(jwtProperties);

        userDetails = User.builder()
                .username(EMAIL)
                .password("{noop}x")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .build();

        UserDetailsService userDetailsService = mock(UserDetailsService.class);
        lenient().when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(userDetails);

        StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, String> valueOps = mock(ValueOperations.class);
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOps);
        lenient().when(valueOps.setIfAbsent(anyString(), anyString(), anyLong(), any()))
                .thenReturn(Boolean.FALSE);

        RedisKeyProperties redisKeyProperties = new RedisKeyProperties();
        redisKeyProperties.setPrefix("test");

        filter = new JwtAuthenticationFilter(
                tokenProvider,
                userDetailsService,
                mock(UserJpaRepository.class),
                redisTemplate,
                redisKeyProperties);
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void accessTokenTuCuaUser_khongDiQuaDuocDuongDanAdmin() throws Exception {
        String token = tokenProvider.generateAccessToken(EMAIL, JwtTokenProvider.CHANNEL_USER);

        assertThat(authenticated(token, ADMIN_PATH))
                .as("token ch=user phải bị chặn ở /api/v1/admin/**")
                .isFalse();
    }

    @Test
    void accessTokenTuCuaUser_vanDungDuocApiUser() throws Exception {
        String token = tokenProvider.generateAccessToken(EMAIL, JwtTokenProvider.CHANNEL_USER);

        assertThat(authenticated(token, USER_PATH)).isTrue();
    }

    @Test
    void accessTokenTuCuaAdmin_dungDuocCaHaiKenh() throws Exception {
        String token = tokenProvider.generateAccessToken(EMAIL, JwtTokenProvider.CHANNEL_ADMIN);

        assertThat(authenticated(token, ADMIN_PATH)).isTrue();
        assertThat(authenticated(token, USER_PATH))
                .as("admin phải dùng được API phía user để test hệ thống")
                .isTrue();
    }

    @Test
    void refreshToken_khongDungDuocThayAccessToken() throws Exception {
        String refresh = tokenProvider.generateRefreshToken(
                EMAIL, "token-id-1", JwtTokenProvider.CHANNEL_ADMIN);

        assertThat(authenticated(refresh, ADMIN_PATH))
                .as("refresh token gửi qua Authorization phải bị loại")
                .isFalse();
        assertThat(authenticated(refresh, USER_PATH)).isFalse();
    }

    @Test
    void accessTokenMangDungClaimTypVaCh() {
        String token = tokenProvider.generateAccessToken(EMAIL, JwtTokenProvider.CHANNEL_ADMIN);

        assertThat(tokenProvider.getAccessTokenTypeFromToken(token))
                .isEqualTo(JwtTokenProvider.TOKEN_TYPE_ACCESS);
        assertThat(tokenProvider.getChannelFromToken(token)).isEqualTo(JwtTokenProvider.CHANNEL_ADMIN);
        assertThat(tokenProvider.getSubjectFromToken(token)).isEqualTo(EMAIL);
    }

    @Test
    void refreshTokenGiuNguyenClaimTypeVaTokenIdCu() {
        String refresh = tokenProvider.generateRefreshToken(
                EMAIL, "token-id-1", JwtTokenProvider.CHANNEL_USER);

        assertThat(tokenProvider.getTokenTypeFromToken(refresh)).isEqualTo("refresh");
        assertThat(tokenProvider.getTokenIdFromToken(refresh)).isEqualTo("token-id-1");
        assertThat(tokenProvider.getChannelFromToken(refresh)).isEqualTo(JwtTokenProvider.CHANNEL_USER);
    }

    @Test
    void accessTokenKieuCu_khongCoTypVaCh_biTuChoi() throws Exception {
        String legacy = legacyAccessToken();

        assertThat(authenticated(legacy, USER_PATH))
                .as("access token phát trước bản này (thiếu typ/ch) phải bị từ chối")
                .isFalse();
        assertThat(authenticated(legacy, ADMIN_PATH)).isFalse();
    }

    /**
     * Dựng lại đúng access token của bản cũ: claims rỗng, chỉ có sub/iat/exp/iss.
     * Chứng minh hành vi breaking đã ghi trong tài liệu.
     */
    private String legacyAccessToken() {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(EMAIL)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + 900_000L))
                .setIssuer("http://localhost:3004")
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .compact();
    }

    /** Chạy filter một lần, trả về true nếu SecurityContext được set. */
    private boolean authenticated(String token, String uri) throws Exception {
        SecurityContextHolder.clearContext();

        MockHttpServletRequest request = new MockHttpServletRequest("GET", uri);
        request.setRequestURI(uri);
        request.addHeader("Authorization", "Bearer " + token);

        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        return SecurityContextHolder.getContext().getAuthentication() != null;
    }
}
