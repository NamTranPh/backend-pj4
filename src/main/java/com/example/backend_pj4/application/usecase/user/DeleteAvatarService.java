package com.example.backend_pj4.application.usecase.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.port.in.user.DeleteAvatarUseCase;
import com.example.backend_pj4.application.port.out.FileStorageService;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.UserRepository;
import com.example.backend_pj4.infrastructure.config.properties.MinioProperties;

@Service
public class DeleteAvatarService implements DeleteAvatarUseCase {

    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final MinioProperties minioProperties;

    public DeleteAvatarService(UserRepository userRepository,
                                FileStorageService fileStorageService,
                                MinioProperties minioProperties) {
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
        this.minioProperties = minioProperties;
    }

    @Override
    @Transactional
    public void execute(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getProfileUrl() != null && !user.getProfileUrl().isBlank()) {
            fileStorageService.delete(minioProperties.getBucketAvatar(), user.getProfileUrl());
        }

        User updated = user.toBuilder().profileUrl(null).build();
        userRepository.save(updated);
    }
}
