package com.example.backend_pj4.application.port.in.admin;

import com.example.backend_pj4.application.command.auth.LogoutCommand;

public interface AdminLogoutUseCase {
    void execute(LogoutCommand command);
}
