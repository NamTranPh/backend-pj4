package com.example.backend_pj4.application.usecase.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.user.UserProfileResult;
import com.example.backend_pj4.application.mapper.UserResultMapper;
import com.example.backend_pj4.application.port.in.user.GetUserByIdUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.repository.UserRepository;

@Service
public class GetUserByIdService implements GetUserByIdUseCase {

    private final UserRepository userRepository;
    private final UserResultMapper userResultMapper;

    public GetUserByIdService(UserRepository userRepository, UserResultMapper userResultMapper) {
        this.userRepository = userRepository;
        this.userResultMapper = userResultMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResult execute(String userId) {
        return userRepository.findById(userId)
                .map(userResultMapper::toResult)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
