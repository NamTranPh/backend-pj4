package com.example.backend_pj4.application.usecase.user;

import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.user.UserProfileResult;
import com.example.backend_pj4.application.mapper.UserResultMapper;
import com.example.backend_pj4.application.port.in.user.UploadAvatarUseCase;
import com.example.backend_pj4.application.port.out.FileStorageService;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.UserRepository;
import com.example.backend_pj4.infrastructure.config.properties.MinioProperties;

@Service
public class UploadAvatarService implements UploadAvatarUseCase {

    private static final long MAX_SIZE_BYTES = 5L * 1024 * 1024; // 5MB
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final MinioProperties minioProperties;
    private final UserResultMapper userResultMapper;

    public UploadAvatarService(UserRepository userRepository,
                               FileStorageService fileStorageService,
                               MinioProperties minioProperties,
                               UserResultMapper userResultMapper) {
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
        this.minioProperties = minioProperties;
        this.userResultMapper = userResultMapper;
    }

    @Override
    @Transactional
    public UserProfileResult execute(String userId, String filename, InputStream data, long size, String contentType) {
        if (size > MAX_SIZE_BYTES) {
            throw new CustomException(ErrorCode.FILE_TOO_LARGE);
        }
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String bucket = minioProperties.getBucketAvatar();

        String ext = (filename != null && filename.contains("."))
                ? filename.substring(filename.lastIndexOf("."))
                : ".jpg";
        String newFilename = UUID.randomUUID() + ext;
        String objectKey = fileStorageService.upload(bucket, "avatars", newFilename, data, size, contentType);

        User updated = user.toBuilder().profileUrl(objectKey).build();
        User saved = userRepository.save(updated);

        return userResultMapper.toResult(saved);
    }
}

