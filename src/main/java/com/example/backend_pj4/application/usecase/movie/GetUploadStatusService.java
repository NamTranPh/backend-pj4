package com.example.backend_pj4.application.usecase.movie;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.common.constants.UploadConstants;
import com.example.backend_pj4.application.dto.movie.UploadPartResult;
import com.example.backend_pj4.application.dto.movie.UploadSessionResult;
import com.example.backend_pj4.application.port.in.movie.GetUploadStatusUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.UploadSession;
import com.example.backend_pj4.domain.repository.UploadSessionRepository;

@Service
public class GetUploadStatusService implements GetUploadStatusUseCase {

    private final UploadSessionRepository uploadSessionRepository;

    public GetUploadStatusService(UploadSessionRepository uploadSessionRepository) {
        this.uploadSessionRepository = uploadSessionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UploadSessionResult execute(String sessionId) {
        UploadSession session = uploadSessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.UPLOAD_SESSION_NOT_FOUND));

        List<UploadPartResult> partResults = session.getParts() != null
                ? session.getParts().stream()
                    .map(p -> new UploadPartResult(p.getPartNumber(), p.getEtag(), p.getSizeBytes(), p.getUploadedAt()))
                    .toList()
                : Collections.emptyList();

        return new UploadSessionResult(
                session.getId(), session.getTargetId(), session.getUploadId(),
                session.getRawFileKey(), "movie-raw", session.getFileSizeBytes(),
                session.getFileName(), UploadConstants.PART_SIZE_BYTES,
                session.getStatus(), partResults,
                session.getExpiresAt(), session.getCreatedAt()
        );
    }
}
