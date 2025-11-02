package com.example.backend_pj4.application.services.user;

import org.springframework.stereotype.Service;

import com.example.backend_pj4.common.base.BaseService;
import com.example.backend_pj4.domain.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeleteUserService extends BaseService {

    private final UserRepository userRepository;

    public void execute(String userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new EntityNotFoundException("Genre not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }
}