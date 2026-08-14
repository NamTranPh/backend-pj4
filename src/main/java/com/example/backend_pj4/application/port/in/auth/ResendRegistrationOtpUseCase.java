package com.example.backend_pj4.application.port.in.auth;

import com.example.backend_pj4.application.command.auth.ResendRegistrationOtpCommand;

public interface ResendRegistrationOtpUseCase {
    void execute(ResendRegistrationOtpCommand command);
}
