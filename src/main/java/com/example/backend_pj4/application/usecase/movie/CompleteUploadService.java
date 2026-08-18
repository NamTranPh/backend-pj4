package com.example.backend_pj4.application.usecase.movie;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.common.constants.UploadConstants;
import com.example.backend_pj4.application.dto.movie.UploadPartResult;
import com.example.backend_pj4.application.dto.movie.UploadSessionResult;
import com.example.backend_pj4.application.port.in.movie.CompleteUploadUseCase;
import com.example.backend_pj4.application.port.out.FileStorageService;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.UploadSession;
import com.example.backend_pj4.domain.repository.UploadSessionRepository;

@Service
public class CompleteUploadService implements CompleteUploadUseCase {

    private static final String BUCKET = "movie-raw";
    private final UploadSessionRepository uploadSessionRepository;
    private final FileStorageService fileStorageService;

    public CompleteUploadService(UploadSessionRepository uploadSessionRepository, FileStorageService fileStorageService) {
        this.uploadSessionRepository = uploadSessionRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional
    public UploadSessionResult execute(String sessionId) {
        UploadSession session = uploadSessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.UPLOAD_SESSION_NOT_FOUND));

        if (!"ACTIVE".equals(session.getStatus())) {
            throw new CustomException(ErrorCode.UPLOAD_ALREADY_COMPLETED);
        }
        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.UPLOAD_SESSION_EXPIRED);
        }
        if (session.getParts() == null || session.getParts().isEmpty()) {
            throw new CustomException(ErrorCode.UPLOAD_MISSING_PARTS);
        }

        List<FileStorageService.PartETag> partETags = session.getParts().stream()
                .sorted(Comparator.comparingInt(p -> p.getPartNumber()))
                .map(p -> new FileStorageService.PartETag(p.getPartNumber(), p.getEtag()))
                .toList();

        fileStorageService.completeMultipartUpload(BUCKET, session.getRawFileKey(), session.getUploadId(), partETags);

        UploadSession completed = session.toBuilder().status("COMPLETED").build();
        UploadSession saved = uploadSessionRepository.save(completed);

        List<UploadPartResult> partResults = saved.getParts().stream()
                .map(p -> new UploadPartResult(p.getPartNumber(), p.getEtag(), p.getSizeBytes(), p.getUploadedAt()))
                .toList();

        return new UploadSessionResult(
                saved.getId(), saved.getTargetId(), saved.getUploadId(),
                saved.getRawFileKey(), BUCKET, saved.getFileSizeBytes(),
                saved.getFileName(), UploadConstants.PART_SIZE_BYTES,
                saved.getStatus(), partResults,
                saved.getExpiresAt(), saved.getCreatedAt()
        );
    }
}
