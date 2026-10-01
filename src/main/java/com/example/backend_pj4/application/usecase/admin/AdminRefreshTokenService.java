package com.example.backend_pj4.application.usecase.admin;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.auth.RefreshTokenCommand;
import com.example.backend_pj4.application.dto.auth.AuthTokenResult;
import com.example.backend_pj4.application.port.in.admin.AdminRefreshTokenUseCase;
import com.example.backend_pj4.application.usecase.auth.RefreshTokenRotator;
import com.example.backend_pj4.infrastructure.security.JwtTokenProvider;

/** Kênh CMS: chỉ nhận refresh token của kênh admin, và chỉ phát vé kênh admin. */
@Service
public class AdminRefreshTokenService implements AdminRefreshTokenUseCase {

    private final RefreshTokenRotator rotator;

    public AdminRefreshTokenService(RefreshTokenRotator rotator) {
        this.rotator = rotator;
    }

    @Override
    @Transactional
    public AuthTokenResult execute(RefreshTokenCommand command) {
        return rotator.rotate(
                command.rawRefreshToken(),
                JwtTokenProvider.CHANNEL_ADMIN,
                command.userAgent(),
                command.ipAddress());
    }
}
