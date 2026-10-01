package com.example.backend_pj4.application.usecase.admin;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.auth.LogoutCommand;
import com.example.backend_pj4.application.port.in.admin.AdminLogoutUseCase;
import com.example.backend_pj4.application.usecase.auth.RefreshTokenRevoker;
import com.example.backend_pj4.infrastructure.security.JwtTokenProvider;

@Service
public class AdminLogoutService implements AdminLogoutUseCase {

    private final RefreshTokenRevoker revoker;

    public AdminLogoutService(RefreshTokenRevoker revoker) {
        this.revoker = revoker;
    }

    @Override
    @Transactional
    public void execute(LogoutCommand command) {
        revoker.revoke(command.rawRefreshToken(), JwtTokenProvider.CHANNEL_ADMIN);
    }
}
