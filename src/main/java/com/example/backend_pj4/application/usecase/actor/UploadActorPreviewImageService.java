package com.example.backend_pj4.application.usecase.actor;

import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.backend_pj4.application.dto.media.MediaUploadResult;
import com.example.backend_pj4.application.port.in.actor.UploadActorPreviewImageUseCase;
import com.example.backend_pj4.application.port.out.FileStorageService;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.infrastructure.config.properties.MinioProperties;

@Service
public class UploadActorPreviewImageService implements UploadActorPreviewImageUseCase {

    private static final long MAX_SIZE_BYTES = 5L * 1024 * 1024; // 5MB
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final FileStorageService fileStorageService;
    private final MinioProperties minioProperties;

    public UploadActorPreviewImageService(FileStorageService fileStorageService, MinioProperties minioProperties) {
        this.fileStorageService = fileStorageService;
        this.minioProperties = minioProperties;
    }

    @Override
    public MediaUploadResult execute(String filename, InputStream data, long size, String contentType) {
        if (size > MAX_SIZE_BYTES) {
            throw new CustomException(ErrorCode.FILE_TOO_LARGE);
        }
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
        }

        String bucket = minioProperties.getBucketActor();
        String folder = "actors";

        String ext = (filename != null && filename.contains("."))
                ? filename.substring(filename.lastIndexOf("."))
                : ".jpg";
        String newFilename = UUID.randomUUID() + ext;
        String objectKey = fileStorageService.upload(bucket, folder, newFilename, data, size, contentType);
        String previewUrl = fileStorageService.getPublicUrl(bucket, objectKey);

        return new MediaUploadResult(previewUrl, objectKey);
    }
}

