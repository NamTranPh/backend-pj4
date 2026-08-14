package com.example.backend_pj4.application.port.in.user;

import java.util.List;

import com.example.backend_pj4.application.dto.user.UserProfileResult;

public interface ListUsersUseCase {
    List<UserProfileResult> execute();
}
