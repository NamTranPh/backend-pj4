package com.example.backend_pj4.application.usecase.auth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.auth.RefreshTokenCommand;
import com.example.backend_pj4.application.dto.auth.AuthTokenResult;
import com.example.backend_pj4.application.port.in.auth.RefreshTokenUseCase;
import com.example.backend_pj4.infrastructure.security.JwtTokenProvider;

/** Kênh người dùng: chỉ nhận refresh token của kênh user, và chỉ phát vé kênh user. */
@Service
public class RefreshTokenService implements RefreshTokenUseCase {

    private final RefreshTokenRotator rotator;

    public RefreshTokenService(RefreshTokenRotator rotator) {
        this.rotator = rotator;
    }

    @Override
    @Transactional
    public AuthTokenResult execute(RefreshTokenCommand command) {
        return rotator.rotate(
                command.rawRefreshToken(),
                JwtTokenProvider.CHANNEL_USER,
                command.userAgent(),
                command.ipAddress());
    }
}
