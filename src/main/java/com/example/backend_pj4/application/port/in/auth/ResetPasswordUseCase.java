package com.example.backend_pj4.application.port.in.auth;

import com.example.backend_pj4.application.command.auth.ResetPasswordCommand;

public interface ResetPasswordUseCase {
    void execute(ResetPasswordCommand command);
}
