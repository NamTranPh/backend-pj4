package com.example.backend_pj4.application.port.in.admin;

import com.example.backend_pj4.application.command.auth.RefreshTokenCommand;
import com.example.backend_pj4.application.dto.auth.AuthTokenResult;

public interface AdminRefreshTokenUseCase {
    AuthTokenResult execute(RefreshTokenCommand command);
}
