package com.example.backend_pj4.application.usecase.auth;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.RefreshToken;
import com.example.backend_pj4.domain.repository.RefreshTokenRepository;
import com.example.backend_pj4.infrastructure.security.JwtTokenProvider;

class RevokeOtherSessionsServiceTest {

    private static final String USER_ID = "usr_1";
    private static final String CURRENT_TOKEN_ID = "tok_123";
    private static final String CURRENT_SESSION_ID = "sess_current";
    private static final String RAW_REFRESH_TOKEN = "valid_raw_refresh_token";

    private RefreshTokenRepository refreshTokenRepository;
    private JwtTokenProvider jwtTokenProvider;
    private RevokeOtherSessionsService service;

    @BeforeEach
    void setUp() {
        refreshTokenRepository = mock(RefreshTokenRepository.class);
        jwtTokenProvider = mock(JwtTokenProvider.class);
        service = new RevokeOtherSessionsService(refreshTokenRepository, jwtTokenProvider);
    }

    @Test
    void hopLe_thiThuHoiCacPhienKhacVaGiuLaiPhienHienTai() {
        when(jwtTokenProvider.getTokenIdFromToken(RAW_REFRESH_TOKEN)).thenReturn(CURRENT_TOKEN_ID);

        RefreshToken currentToken = RefreshToken.builder()
                .userId(USER_ID)
                .tokenId(CURRENT_TOKEN_ID)
                .sessionId(CURRENT_SESSION_ID)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .revokedAt(null)
                .build();
        when(refreshTokenRepository.findByTokenId(CURRENT_TOKEN_ID)).thenReturn(Optional.of(currentToken));

        service.execute(USER_ID, RAW_REFRESH_TOKEN);

        verify(refreshTokenRepository).revokeAllByUserIdExceptSession(USER_ID, CURRENT_SESSION_ID);
    }

    @Test
    void thieuRefreshToken_thiBaoLoi() {
        assertThatThrownBy(() -> service.execute(USER_ID, null))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(ErrorCode.REFRESH_TOKEN_INVALID_OR_EXPIRED);

        verify(refreshTokenRepository, never()).revokeAllByUserIdExceptSession(anyString(), anyString());
    }

    @Test
    void tokenCuaNguoiDungKhac_thiBaoLoi() {
        when(jwtTokenProvider.getTokenIdFromToken(RAW_REFRESH_TOKEN)).thenReturn(CURRENT_TOKEN_ID);

        RefreshToken tokenCuaNguoiKhac = RefreshToken.builder()
                .userId("usr_other")
                .tokenId(CURRENT_TOKEN_ID)
                .sessionId(CURRENT_SESSION_ID)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .revokedAt(null)
                .build();
        when(refreshTokenRepository.findByTokenId(CURRENT_TOKEN_ID)).thenReturn(Optional.of(tokenCuaNguoiKhac));

        assertThatThrownBy(() -> service.execute(USER_ID, RAW_REFRESH_TOKEN))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(ErrorCode.REFRESH_TOKEN_INVALID_OR_EXPIRED);

        verify(refreshTokenRepository, never()).revokeAllByUserIdExceptSession(anyString(), anyString());
    }

    @Test
    void tokenDaBiThuHoi_thiBaoLoi() {
        when(jwtTokenProvider.getTokenIdFromToken(RAW_REFRESH_TOKEN)).thenReturn(CURRENT_TOKEN_ID);

        RefreshToken tokenDaRevoke = RefreshToken.builder()
                .userId(USER_ID)
                .tokenId(CURRENT_TOKEN_ID)
                .sessionId(CURRENT_SESSION_ID)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .revokedAt(LocalDateTime.now().minusHours(1))
                .build();
        when(refreshTokenRepository.findByTokenId(CURRENT_TOKEN_ID)).thenReturn(Optional.of(tokenDaRevoke));

        assertThatThrownBy(() -> service.execute(USER_ID, RAW_REFRESH_TOKEN))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(ErrorCode.REFRESH_TOKEN_INVALID_OR_EXPIRED);

        verify(refreshTokenRepository, never()).revokeAllByUserIdExceptSession(anyString(), anyString());
    }
}
