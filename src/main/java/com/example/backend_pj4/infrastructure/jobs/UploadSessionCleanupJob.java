package com.example.backend_pj4.infrastructure.jobs;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.backend_pj4.application.port.out.FileStorageService;
import com.example.backend_pj4.domain.model.UploadSession;
import com.example.backend_pj4.domain.repository.UploadSessionRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UploadSessionCleanupJob {

    private final UploadSessionRepository uploadSessionRepository;
    private final FileStorageService fileStorageService;

    public UploadSessionCleanupJob(UploadSessionRepository uploadSessionRepository, FileStorageService fileStorageService) {
        this.uploadSessionRepository = uploadSessionRepository;
        this.fileStorageService = fileStorageService;
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupExpiredUploadSessions() {
        List<UploadSession> expired = uploadSessionRepository.findExpired();
        if (expired.isEmpty()) return;

        log.info("Cleaning up {} expired upload sessions", expired.size());
        for (UploadSession session : expired) {
            try {
                fileStorageService.abortMultipartUpload("movie-raw", session.getRawFileKey(), session.getUploadId());
                uploadSessionRepository.deleteById(session.getId());
                log.info("Cleaned up expired upload session: {}", session.getId());
            } catch (Exception e) {
                log.error("Failed to clean up upload session: {}", session.getId(), e);
            }
        }
    }
}
