package com.example.backend_pj4.application.port.in.auth;

import com.example.backend_pj4.application.dto.user.UserProfileResult;

public interface GetCurrentUserUseCase {
    UserProfileResult execute(String email);
}
