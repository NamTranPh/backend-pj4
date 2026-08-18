package com.example.backend_pj4.application.usecase.movie;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.common.constants.UploadConstants;
import com.example.backend_pj4.application.dto.movie.PresignedUrlResult;
import com.example.backend_pj4.application.port.in.movie.GetPartUrlsUseCase;
import com.example.backend_pj4.application.port.out.FileStorageService;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.UploadSession;
import com.example.backend_pj4.domain.repository.UploadSessionRepository;

@Service
public class GetPartUrlsService implements GetPartUrlsUseCase {

    private static final String BUCKET = "movie-raw";
    private final UploadSessionRepository uploadSessionRepository;
    private final FileStorageService fileStorageService;

    public GetPartUrlsService(UploadSessionRepository uploadSessionRepository, FileStorageService fileStorageService) {
        this.uploadSessionRepository = uploadSessionRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PresignedUrlResult> execute(String sessionId, List<Integer> partNumbers) {
        UploadSession session = uploadSessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.UPLOAD_SESSION_NOT_FOUND));

        if (!"ACTIVE".equals(session.getStatus())) {
            throw new CustomException(ErrorCode.UPLOAD_ALREADY_COMPLETED);
        }
        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.UPLOAD_SESSION_EXPIRED);
        }

        return partNumbers.stream()
                .map(partNumber -> {
                    String url = fileStorageService.getPresignedUploadUrl(
                            BUCKET, session.getRawFileKey(), session.getUploadId(),
                            partNumber, UploadConstants.PRESIGNED_URL_EXPIRY_SECONDS);
                    return new PresignedUrlResult(partNumber, url);
                })
                .toList();
    }
}
