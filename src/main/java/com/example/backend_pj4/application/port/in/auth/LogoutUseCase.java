package com.example.backend_pj4.application.port.in.auth;

import com.example.backend_pj4.application.command.auth.LogoutCommand;

public interface LogoutUseCase {
    void execute(LogoutCommand command);
}
