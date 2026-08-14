package com.example.backend_pj4.application.usecase.auth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.auth.LogoutCommand;
import com.example.backend_pj4.application.port.in.auth.LogoutUseCase;
import com.example.backend_pj4.application.port.out.RefreshTokenStore;
import com.example.backend_pj4.infrastructure.security.JwtTokenProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LogoutService implements LogoutUseCase {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenStore refreshTokenStore;

    public LogoutService(JwtTokenProvider jwtTokenProvider, RefreshTokenStore refreshTokenStore) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenStore = refreshTokenStore;
    }

    @Override
    @Transactional
    public void execute(LogoutCommand command) {
        if (command.rawRefreshToken() == null || command.rawRefreshToken().isBlank()) {
            return;
        }

        try {
            String tokenId = jwtTokenProvider.getTokenIdFromToken(command.rawRefreshToken());
            if (tokenId != null) {
                refreshTokenStore.revokeByTokenId(tokenId);
            }
        } catch (Exception e) {
            log.warn("Failed to revoke refresh token during logout", e);
        }
    }
}
