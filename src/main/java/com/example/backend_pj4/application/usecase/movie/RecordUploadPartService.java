package com.example.backend_pj4.application.usecase.movie;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.movie.RecordUploadPartCommand;
import com.example.backend_pj4.application.port.in.movie.RecordUploadPartUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.UploadSession;
import com.example.backend_pj4.domain.repository.UploadSessionRepository;

@Service
public class RecordUploadPartService implements RecordUploadPartUseCase {

    private final UploadSessionRepository uploadSessionRepository;

    public RecordUploadPartService(UploadSessionRepository uploadSessionRepository) {
        this.uploadSessionRepository = uploadSessionRepository;
    }

    @Override
    @Transactional
    public void execute(RecordUploadPartCommand command) {
        UploadSession session = uploadSessionRepository.findById(command.sessionId())
                .orElseThrow(() -> new CustomException(ErrorCode.UPLOAD_SESSION_NOT_FOUND));

        if (!"ACTIVE".equals(session.getStatus())) {
            throw new CustomException(ErrorCode.UPLOAD_ALREADY_COMPLETED);
        }
        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.UPLOAD_SESSION_EXPIRED);
        }

        uploadSessionRepository.addPart(command.sessionId(), command.partNumber(), command.etag(), command.sizeBytes());
    }
}
