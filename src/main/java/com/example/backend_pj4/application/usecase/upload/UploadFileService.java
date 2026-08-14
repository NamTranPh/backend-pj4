package com.example.backend_pj4.application.usecase.upload;

import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.backend_pj4.application.command.upload.UploadFileCommand;
import com.example.backend_pj4.application.dto.upload.UploadResult;
import com.example.backend_pj4.application.port.in.upload.UploadFileUseCase;
import com.example.backend_pj4.application.port.out.FileStorageService;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.UploadType;
import com.example.backend_pj4.common.constants.enums.UserRole;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.infrastructure.config.properties.MinioProperties;

@Service
public class UploadFileService implements UploadFileUseCase {

    private static final Set<String> ALLOWED_AVATAR_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final Set<String> ALLOWED_POSTER_TYPES = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");
    private static final Set<String> ALLOWED_VIDEO_TYPES = Set.of("video/mp4", "video/quicktime", "video/x-matroska", "video/webm");

    private final FileStorageService fileStorageService;
    private final MinioProperties minioProperties;

    public UploadFileService(FileStorageService fileStorageService,
                             MinioProperties minioProperties) {
        this.fileStorageService = fileStorageService;
        this.minioProperties = minioProperties;
    }

    @Override
    public UploadResult execute(UploadFileCommand command) {
        UploadType type = command.type();

        // Chỉ ADMIN mới được upload POSTER hoặc VIDEO
        if ((type == UploadType.POSTER || type == UploadType.VIDEO) && command.userRole() != UserRole.ADMIN) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        String contentType = command.contentType();
        validateContentType(type, contentType);

        String bucket = resolveBucket(type);
        String folder = type.name().toLowerCase();
        String ext = extractExtension(command.originalFilename(), contentType);
        String filename = UUID.randomUUID() + "." + ext;

        String objectKey = fileStorageService.upload(bucket, folder, filename, command.data(), command.size(), contentType);
        String publicUrl = fileStorageService.getPublicUrl(bucket, objectKey);

        return new UploadResult(publicUrl, objectKey);
    }

    private void validateContentType(UploadType type, String contentType) {
        Set<String> allowed = switch (type) {
            case AVATAR -> ALLOWED_AVATAR_TYPES;
            case POSTER -> ALLOWED_POSTER_TYPES;
            case VIDEO -> ALLOWED_VIDEO_TYPES;
        };

        if (contentType == null || !allowed.contains(contentType.toLowerCase())) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
    }

    private String resolveBucket(UploadType type) {
        return switch (type) {
            case AVATAR -> minioProperties.getBucketAvatar();
            case POSTER -> minioProperties.getBucketPublic();
            case VIDEO -> minioProperties.getBucketRaw();
        };
    }

    private String extractExtension(String filename, String contentType) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        }
        if (contentType != null) {
            return switch (contentType.toLowerCase()) {
                case "image/png" -> "png";
                case "image/webp" -> "webp";
                case "image/gif" -> "gif";
                case "video/mp4" -> "mp4";
                case "video/quicktime" -> "mov";
                case "video/x-matroska" -> "mkv";
                case "video/webm" -> "webm";
                default -> "jpg";
            };
        }
        return "jpg";
    }
}
