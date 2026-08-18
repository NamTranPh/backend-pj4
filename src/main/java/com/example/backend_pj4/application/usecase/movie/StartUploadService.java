package com.example.backend_pj4.application.usecase.movie;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.movie.StartUploadCommand;
import com.example.backend_pj4.common.constants.UploadConstants;
import com.example.backend_pj4.application.dto.movie.UploadPartResult;
import com.example.backend_pj4.application.dto.movie.UploadSessionResult;
import com.example.backend_pj4.application.port.in.movie.StartUploadUseCase;
import com.example.backend_pj4.application.port.out.FileStorageService;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.UploadSession;
import com.example.backend_pj4.domain.repository.UploadSessionRepository;

@Service
public class StartUploadService implements StartUploadUseCase {

    private static final String BUCKET = "movie-raw";
    private final UploadSessionRepository uploadSessionRepository;
    private final FileStorageService fileStorageService;

    public StartUploadService(UploadSessionRepository uploadSessionRepository, FileStorageService fileStorageService) {
        this.uploadSessionRepository = uploadSessionRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional
    public UploadSessionResult execute(StartUploadCommand command) {
        if (command.fileSizeBytes() <= 0 || command.fileSizeBytes() > UploadConstants.MAX_UPLOAD_SIZE_BYTES) {
            throw new CustomException(ErrorCode.UPLOAD_INVALID_FILE_SIZE);
        }

        String rawFileKey = command.targetType() + "s/" + command.targetId() + "/raw/" + UUID.randomUUID() + ".mp4";
        String uploadId = fileStorageService.initiateMultipartUpload(BUCKET, rawFileKey);

        UploadSession session = UploadSession.builder()
                .targetId(command.targetId())
                .targetType(command.targetType())
                .rawFileKey(rawFileKey)
                .uploadId(uploadId)
                .fileSizeBytes(command.fileSizeBytes())
                .fileName(command.fileName())
                .status("ACTIVE")
                .parts(Collections.emptyList())
                .expiresAt(LocalDateTime.now().plusHours(UploadConstants.UPLOAD_SESSION_TTL_HOURS))
                .build();

        UploadSession saved = uploadSessionRepository.save(session);

        return new UploadSessionResult(
                saved.getId(), saved.getTargetId(), saved.getUploadId(),
                saved.getRawFileKey(), BUCKET, saved.getFileSizeBytes(),
                saved.getFileName(), UploadConstants.PART_SIZE_BYTES,
                saved.getStatus(), Collections.emptyList(),
                saved.getExpiresAt(), saved.getCreatedAt()
        );
    }
}
