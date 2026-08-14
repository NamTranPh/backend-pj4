package com.example.backend_pj4.application.usecase.user;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.user.UserProfileResult;
import com.example.backend_pj4.application.mapper.UserResultMapper;
import com.example.backend_pj4.application.port.in.user.ListUsersUseCase;
import com.example.backend_pj4.domain.repository.UserRepository;

@Service
public class ListUsersService implements ListUsersUseCase {

    private final UserRepository userRepository;

    public ListUsersService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserProfileResult> execute() {
        return userRepository.findAll().stream()
                .map(UserResultMapper::toResult)
                .toList();
    }
}
