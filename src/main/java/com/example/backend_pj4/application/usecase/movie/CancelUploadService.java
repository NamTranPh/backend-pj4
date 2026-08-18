package com.example.backend_pj4.application.usecase.movie;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.port.in.movie.CancelUploadUseCase;
import com.example.backend_pj4.application.port.out.FileStorageService;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.UploadSession;
import com.example.backend_pj4.domain.repository.UploadSessionRepository;

@Service
public class CancelUploadService implements CancelUploadUseCase {

    private static final String BUCKET = "movie-raw";
    private final UploadSessionRepository uploadSessionRepository;
    private final FileStorageService fileStorageService;

    public CancelUploadService(UploadSessionRepository uploadSessionRepository, FileStorageService fileStorageService) {
        this.uploadSessionRepository = uploadSessionRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional
    public void execute(String sessionId) {
        UploadSession session = uploadSessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.UPLOAD_SESSION_NOT_FOUND));

        if ("ACTIVE".equals(session.getStatus())) {
            fileStorageService.abortMultipartUpload(BUCKET, session.getRawFileKey(), session.getUploadId());
        }

        uploadSessionRepository.deleteById(sessionId);
    }
}
