package com.example.backend_pj4.application.port.in.auth;

import com.example.backend_pj4.application.command.auth.LoginCommand;
import com.example.backend_pj4.application.dto.auth.AuthTokenResult;

public interface LoginUseCase {
    AuthTokenResult execute(LoginCommand command);
}
