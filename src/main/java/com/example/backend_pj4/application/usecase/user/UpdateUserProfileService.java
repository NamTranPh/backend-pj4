package com.example.backend_pj4.application.usecase.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.user.UpdateProfileCommand;
import com.example.backend_pj4.application.dto.user.UserProfileResult;
import com.example.backend_pj4.application.mapper.UserResultMapper;
import com.example.backend_pj4.application.port.in.user.UpdateUserProfileUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.UserRepository;

@Service
public class UpdateUserProfileService implements UpdateUserProfileUseCase {

    private final UserRepository userRepository;
    private final UserResultMapper userResultMapper;

    public UpdateUserProfileService(UserRepository userRepository, UserResultMapper userResultMapper) {
        this.userRepository = userRepository;
        this.userResultMapper = userResultMapper;
    }

    @Override
    @Transactional
    public UserProfileResult execute(UpdateProfileCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        User.UserBuilder builder = user.toBuilder();
        if (command.name() != null) builder.name(command.name());
        if (command.phone() != null) builder.phone(command.phone());
        if (command.address() != null) builder.address(command.address());
        if (command.profileUrl() != null) builder.profileUrl(command.profileUrl());

        User saved = userRepository.save(builder.build());
        return userResultMapper.toResult(saved);
    }
}
