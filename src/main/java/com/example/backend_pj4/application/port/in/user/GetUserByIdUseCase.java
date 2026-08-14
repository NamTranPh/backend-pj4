package com.example.backend_pj4.application.port.in.user;

import com.example.backend_pj4.application.dto.user.UserProfileResult;

public interface GetUserByIdUseCase {
    UserProfileResult execute(String userId);
}
