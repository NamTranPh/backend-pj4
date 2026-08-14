package com.example.backend_pj4.application.port.in.auth;

import com.example.backend_pj4.application.command.auth.RefreshTokenCommand;
import com.example.backend_pj4.application.dto.auth.AuthTokenResult;

public interface RefreshTokenUseCase {
    AuthTokenResult execute(RefreshTokenCommand command);
}
