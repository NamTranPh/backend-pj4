package com.example.backend_pj4.application.usecase.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.repository.RefreshTokenRepository;

/** Phần GHI của quản lý phiên, tách khỏi phần đọc khi chia CQRS. */
class RevokeSessionServiceTest {

    private static final String USER_ID = "usr_1";

    private RefreshTokenRepository refreshTokenRepository;
    private RevokeSessionService service;

    @BeforeEach
    void setUp() {
        refreshTokenRepository = mock(RefreshTokenRepository.class);
        service = new RevokeSessionService(refreshTokenRepository);
    }

    @Test
    void dangXuatPhienCuaChinhMinh_thiThuHoi() {
        when(refreshTokenRepository.existsActiveSession(USER_ID, "s1")).thenReturn(true);

        service.execute(USER_ID, "s1");

        verify(refreshTokenRepository).revokeAllBySessionId("s1");
    }

    @Test
    void khongDangXuatDuocPhienCuaNguoiKHAC() {
        when(refreshTokenRepository.existsActiveSession(USER_ID, "s9")).thenReturn(false);

        try {
            service.execute(USER_ID, "s9");
            throw new AssertionError("mong đợi CustomException");
        } catch (CustomException e) {
            assertThat(e.getErrorCode())
                    .as("trả cùng lỗi với phiên không tồn tại, không tiết lộ sessionId nào có thật")
                    .isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        }

        verify(refreshTokenRepository, never()).revokeAllBySessionId(anyString());
    }
}
