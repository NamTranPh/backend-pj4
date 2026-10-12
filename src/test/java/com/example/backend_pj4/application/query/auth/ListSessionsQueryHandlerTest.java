package com.example.backend_pj4.application.query.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.backend_pj4.application.dto.auth.SessionResult;
import com.example.backend_pj4.common.constants.enums.SortOrder;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.RefreshToken;
import com.example.backend_pj4.domain.repository.RefreshTokenRepository;
import com.example.backend_pj4.domain.repository.filter.SessionFilter;
import com.example.backend_pj4.infrastructure.config.properties.JwtProperties;
import com.example.backend_pj4.infrastructure.security.JwtTokenProvider;

/** Khoá hành vi liệt kê của màn hình quản lý thiết bị đang đăng nhập. */
class ListSessionsQueryHandlerTest {

    private static final String USER_ID = "usr_1";
    private static final String EMAIL = "nam@gmail.com";
    private static final PageQuery QUERY = new PageQuery(0, 15, "lastUsedAt", SortOrder.desc);
    private static final SessionFilter FILTER = new SessionFilter(USER_ID);

    private RefreshTokenRepository refreshTokenRepository;
    private JwtTokenProvider tokenProvider;
    private ListSessionsQueryHandler handler;

    @BeforeEach
    void setUp() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("test-secret-test-secret-test-secret-test-secret-0123456789");
        jwtProperties.setExpiration(900_000L);
        jwtProperties.setRefreshExpiration(2_592_000_000L);
        jwtProperties.setIssuer("http://localhost:3004");
        tokenProvider = new JwtTokenProvider(jwtProperties);

        refreshTokenRepository = mock(RefreshTokenRepository.class);
        handler = new ListSessionsQueryHandler(refreshTokenRepository, tokenProvider);
    }

    private RefreshToken session(String sessionId, String tokenId, String userAgent) {
        return RefreshToken.builder()
                .userId(USER_ID)
                .tokenId(tokenId)
                .sessionId(sessionId)
                .admin(false)
                .userAgent(userAgent)
                .ipAddress("1.2.3.4")
                .lastUsedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(30))
                .build();
    }

    private void givenSessions(RefreshToken... rows) {
        List<RefreshToken> list = List.of(rows);
        lenient().when(refreshTokenRepository.find(any(SessionFilter.class), any(PageQuery.class)))
                .thenReturn(new PageResult<>(list, list.size(), 1, 15, 1));
    }

    private List<SessionResult> listSessions(String cookie) {
        return handler.execute(FILTER, cookie, QUERY).items();
    }

    @Test
    void lietKeDungSoPhien_vaThongTinThietBi() {
        givenSessions(session("s1", "t1", "Chrome / Windows"), session("s2", "t2", "Safari / iPhone"));

        List<SessionResult> sessions = listSessions(null);

        assertThat(sessions).hasSize(2);
        assertThat(sessions).extracting(SessionResult::userAgent)
                .containsExactly("Chrome / Windows", "Safari / iPhone");
        assertThat(sessions).allMatch(s -> !s.current(), "không có cookie thì không đánh dấu phiên nào");
    }

    @Test
    void danhDauDungPhienHienTai_duaVaoRefreshTokenTrongCookie() {
        givenSessions(session("s1", "t1", "Chrome"), session("s2", "t2", "Safari"));
        when(refreshTokenRepository.findByTokenId("t2"))
                .thenReturn(Optional.of(session("s2", "t2", "Safari")));

        String cookie = tokenProvider.generateRefreshToken(EMAIL, "t2", JwtTokenProvider.CHANNEL_USER);

        assertThat(listSessions(cookie))
                .filteredOn(SessionResult::current)
                .extracting(SessionResult::sessionId)
                .containsExactly("s2");
    }

    @Test
    void cookieHong_vanLietKeDuoc_chiMatNhanThietBiNay() {
        givenSessions(session("s1", "t1", "Chrome"));

        List<SessionResult> sessions = listSessions("khong-phai-jwt");

        assertThat(sessions).hasSize(1);
        assertThat(sessions.get(0).current()).isFalse();
    }

    @Test
    void khongCoPhienNao_traDanhSachRong() {
        givenSessions();

        assertThat(listSessions(null)).isEmpty();
    }
}
