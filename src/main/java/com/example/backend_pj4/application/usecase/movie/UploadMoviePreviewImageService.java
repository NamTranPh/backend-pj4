package com.example.backend_pj4.application.usecase.movie;

import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.backend_pj4.application.dto.media.MediaUploadResult;
import com.example.backend_pj4.application.port.in.movie.UploadMoviePreviewImageUseCase;
import com.example.backend_pj4.application.port.out.FileStorageService;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.infrastructure.config.properties.MinioProperties;

@Service
public class UploadMoviePreviewImageService implements UploadMoviePreviewImageUseCase {

    private static final long MAX_SIZE_BYTES = 10L * 1024 * 1024; // 10MB
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");

    private final FileStorageService fileStorageService;
    private final MinioProperties minioProperties;

    public UploadMoviePreviewImageService(FileStorageService fileStorageService, MinioProperties minioProperties) {
        this.fileStorageService = fileStorageService;
        this.minioProperties = minioProperties;
    }

    @Override
    public MediaUploadResult execute(String imageType, String filename, InputStream data, long size, String contentType) {
        if (size > MAX_SIZE_BYTES) {
            throw new CustomException(ErrorCode.FILE_TOO_LARGE);
        }
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
        }

        String bucket = minioProperties.getBucketPublic();
        String folder = "posters";
        if (imageType != null && imageType.equalsIgnoreCase("backdrop")) {
            folder = "backdrops";
        }

        String ext = (filename != null && filename.contains("."))
                ? filename.substring(filename.lastIndexOf("."))
                : ".jpg";
        String newFilename = UUID.randomUUID() + ext;
        String objectKey = fileStorageService.upload(bucket, folder, newFilename, data, size, contentType);
        String previewUrl = fileStorageService.getPublicUrl(bucket, objectKey);

        return new MediaUploadResult(previewUrl, objectKey);
    }
}

