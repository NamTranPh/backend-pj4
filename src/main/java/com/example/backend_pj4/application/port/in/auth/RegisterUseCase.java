package com.example.backend_pj4.application.port.in.auth;

import com.example.backend_pj4.application.command.auth.RegisterCommand;
import com.example.backend_pj4.application.dto.auth.RegisterResult;

public interface RegisterUseCase {
    RegisterResult execute(RegisterCommand command);
}
