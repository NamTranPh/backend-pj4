package com.example.backend_pj4.application.usecase.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.AccountStatus;
import com.example.backend_pj4.common.constants.enums.UserRole;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.RefreshToken;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.RefreshTokenRepository;
import com.example.backend_pj4.domain.repository.UserRepository;
import com.example.backend_pj4.infrastructure.config.properties.JwtProperties;
import com.example.backend_pj4.infrastructure.security.JwtTokenProvider;

/**
 * Khoá hai hành vi của cơ chế xoay vòng dùng chung:
 * kênh phải khớp theo CẢ HAI chiều, và rotation phải giữ nguyên sessionId.
 */
class RefreshTokenRotatorTest {

    private static final String EMAIL = "nam@gmail.com";
    private static final String SESSION_ID = "sess-1";

    private RefreshTokenRepository refreshTokenRepository;
    private JwtTokenProvider tokenProvider;
    private RefreshTokenRotator rotator;

    @BeforeEach
    void setUp() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("test-secret-test-secret-test-secret-test-secret-0123456789");
        jwtProperties.setExpiration(900_000L);
        jwtProperties.setRefreshExpiration(2_592_000_000L);
        jwtProperties.setIssuer("http://localhost:3004");
        tokenProvider = new JwtTokenProvider(jwtProperties);

        refreshTokenRepository = mock(RefreshTokenRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        lenient().when(userRepository.findById("usr_1")).thenReturn(Optional.of(User.builder()
                .id("usr_1")
                .email(EMAIL)
                .role(UserRole.ADMIN)
                .accountStatus(AccountStatus.ACTIVE)
                .isBanned(false)
                .build()));
        lenient().when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        rotator = new RefreshTokenRotator(tokenProvider, refreshTokenRepository, userRepository, jwtProperties);
    }

    /** Dựng một refresh token hợp lệ kèm row tương ứng trong DB. */
    private String givenStoredToken(String tokenId, boolean admin) {
        String channel = JwtTokenProvider.channelOf(admin);
        when(refreshTokenRepository.findByTokenId(tokenId)).thenReturn(Optional.of(RefreshToken.builder()
                .id("rt_1")
                .userId("usr_1")
                .tokenId(tokenId)
                .sessionId(SESSION_ID)
                .admin(admin)
                .expiresAt(LocalDateTime.now().plusDays(30))
                .userAgent("Chrome cũ")
                .ipAddress("10.0.0.1")
                .build()));
        return tokenProvider.generateRefreshToken(EMAIL, tokenId, channel);
    }

    private ErrorCode errorOf(Runnable action) {
        try {
            action.run();
            throw new AssertionError("mong đợi CustomException nhưng không có lỗi nào");
        } catch (CustomException e) {
            return e.getErrorCode();
        }
    }

    @Test
    void tokenAdmin_guiVaoCuaUser_biTuChoi() {
        String adminToken = givenStoredToken("tok-admin", true);

        assertThat(errorOf(() -> rotator.rotate(adminToken, JwtTokenProvider.CHANNEL_USER, null, null)))
                .as("cửa user không được phát vé admin")
                .isEqualTo(ErrorCode.REFRESH_TOKEN_REVOKED);
    }

    @Test
    void tokenUser_guiVaoCuaAdmin_biTuChoi() {
        String userToken = givenStoredToken("tok-user", false);

        assertThat(errorOf(() -> rotator.rotate(userToken, JwtTokenProvider.CHANNEL_ADMIN, null, null)))
                .isEqualTo(ErrorCode.REFRESH_TOKEN_REVOKED);
    }

    @Test
    void rotation_giuNguyenSessionId_doiTokenId_vaRevokeTokenCu() {
        String userToken = givenStoredToken("tok-user", false);

        rotator.rotate(userToken, JwtTokenProvider.CHANNEL_USER, "Chrome mới", "10.0.0.2");

        verify(refreshTokenRepository).revokeByTokenId("tok-user");

        ArgumentCaptor<RefreshToken> saved = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(saved.capture());

        assertThat(saved.getValue().getSessionId())
                .as("rotation không mở phiên mới, vẫn là cùng một thiết bị")
                .isEqualTo(SESSION_ID);
        assertThat(saved.getValue().getTokenId()).isNotEqualTo("tok-user");
        assertThat(saved.getValue().getUserAgent()).isEqualTo("Chrome mới");
        assertThat(saved.getValue().getIpAddress()).isEqualTo("10.0.0.2");
        assertThat(saved.getValue().getLastUsedAt()).isNotNull();
    }

    @Test
    void tokenDaRevoke_biTuChoi() {
        when(refreshTokenRepository.findByTokenId("tok-revoked")).thenReturn(Optional.of(RefreshToken.builder()
                .userId("usr_1")
                .tokenId("tok-revoked")
                .sessionId(SESSION_ID)
                .admin(false)
                .expiresAt(LocalDateTime.now().plusDays(30))
                .revokedAt(LocalDateTime.now().minusMinutes(1))
                .build()));
        String token = tokenProvider.generateRefreshToken(EMAIL, "tok-revoked", JwtTokenProvider.CHANNEL_USER);

        assertThat(errorOf(() -> rotator.rotate(token, JwtTokenProvider.CHANNEL_USER, null, null)))
                .isEqualTo(ErrorCode.REFRESH_TOKEN_REVOKED);
    }

    @Test
    void accessTokenGuiVaoEndpointRefresh_biTuChoi() {
        String accessToken = tokenProvider.generateAccessToken(EMAIL, JwtTokenProvider.CHANNEL_USER);

        assertThat(errorOf(() -> rotator.rotate(accessToken, JwtTokenProvider.CHANNEL_USER, null, null)))
                .isEqualTo(ErrorCode.REFRESH_TOKEN_INVALID_OR_EXPIRED);
    }
}
