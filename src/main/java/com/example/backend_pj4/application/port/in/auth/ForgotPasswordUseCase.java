package com.example.backend_pj4.application.port.in.auth;

import com.example.backend_pj4.application.command.auth.ForgotPasswordCommand;

public interface ForgotPasswordUseCase {
    void execute(ForgotPasswordCommand command);
}
