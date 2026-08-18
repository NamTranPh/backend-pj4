package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.domain.model.UploadSession;

public interface UploadSessionRepository {
    UploadSession save(UploadSession session);
    Optional<UploadSession> findById(String id);
    Optional<UploadSession> findByTargetIdAndStatus(String targetId, String status);
    void deleteById(String id);
    List<UploadSession> findExpired();
    void addPart(String sessionId, int partNumber, String etag, long sizeBytes);
}
