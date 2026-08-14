package com.example.backend_pj4.application.port.in.admin;

import com.example.backend_pj4.application.command.auth.LoginCommand;
import com.example.backend_pj4.application.dto.auth.AuthTokenResult;

public interface AdminLoginUseCase {
    AuthTokenResult execute(LoginCommand command);
}
