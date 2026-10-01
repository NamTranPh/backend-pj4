package com.example.backend_pj4.application.usecase.auth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.auth.LogoutCommand;
import com.example.backend_pj4.application.port.in.auth.LogoutUseCase;
import com.example.backend_pj4.infrastructure.security.JwtTokenProvider;

@Service
public class LogoutService implements LogoutUseCase {

    private final RefreshTokenRevoker revoker;

    public LogoutService(RefreshTokenRevoker revoker) {
        this.revoker = revoker;
    }

    @Override
    @Transactional
    public void execute(LogoutCommand command) {
        revoker.revoke(command.rawRefreshToken(), JwtTokenProvider.CHANNEL_USER);
    }
}
