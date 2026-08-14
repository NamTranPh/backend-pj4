package com.example.backend_pj4.application.port.in.user;

import com.example.backend_pj4.application.command.user.UpdateProfileCommand;
import com.example.backend_pj4.application.dto.user.UserProfileResult;

public interface UpdateUserProfileUseCase {
    UserProfileResult execute(UpdateProfileCommand command);
}
