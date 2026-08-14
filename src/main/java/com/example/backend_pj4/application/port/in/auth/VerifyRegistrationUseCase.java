package com.example.backend_pj4.application.port.in.auth;

import com.example.backend_pj4.application.command.auth.VerifyRegistrationCommand;

public interface VerifyRegistrationUseCase {
    void execute(VerifyRegistrationCommand command);
}
