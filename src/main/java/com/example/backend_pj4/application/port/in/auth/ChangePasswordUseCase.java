package com.example.backend_pj4.application.port.in.auth;

import com.example.backend_pj4.application.command.auth.ChangePasswordCommand;

public interface ChangePasswordUseCase {
    void execute(ChangePasswordCommand command);
}
