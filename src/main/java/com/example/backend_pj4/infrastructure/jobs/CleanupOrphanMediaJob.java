package com.example.backend_pj4.infrastructure.jobs;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.backend_pj4.application.port.out.FileStorageService;
import com.example.backend_pj4.application.port.out.FileStorageService.StorageItem;
import com.example.backend_pj4.infrastructure.config.properties.MinioProperties;
import com.example.backend_pj4.infrastructure.database.repositories.ActorJpaRepository;
import com.example.backend_pj4.infrastructure.database.repositories.EpisodeJpaRepository;
import com.example.backend_pj4.infrastructure.database.repositories.MovieJpaRepository;
import com.example.backend_pj4.infrastructure.database.repositories.UserJpaRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CleanupOrphanMediaJob {

    private final FileStorageService fileStorageService;
    private final MinioProperties minioProperties;
    private final MovieJpaRepository movieJpaRepository;
    private final EpisodeJpaRepository episodeJpaRepository;
    private final ActorJpaRepository actorJpaRepository;
    private final UserJpaRepository userJpaRepository;

    public CleanupOrphanMediaJob(
            FileStorageService fileStorageService,
            MinioProperties minioProperties,
            MovieJpaRepository movieJpaRepository,
            EpisodeJpaRepository episodeJpaRepository,
            ActorJpaRepository actorJpaRepository,
            UserJpaRepository userJpaRepository
    ) {
        this.fileStorageService = fileStorageService;
        this.minioProperties = minioProperties;
        this.movieJpaRepository = movieJpaRepository;
        this.episodeJpaRepository = episodeJpaRepository;
        this.actorJpaRepository = actorJpaRepository;
        this.userJpaRepository = userJpaRepository;
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupOrphanMedia() {
        log.info("Starting scheduled cleanup for orphan media files on MinIO...");
        Instant threshold = Instant.now().minus(24, ChronoUnit.HOURS);

        cleanupMoviePublicBucket(threshold);
        cleanupActorAvatarsBucket(threshold);
        cleanupUserAvatarsBucket(threshold);

        log.info("Finished scheduled cleanup for orphan media files.");
    }

    private void cleanupMoviePublicBucket(Instant threshold) {
        String bucket = minioProperties.getBucketPublic();
        List<StorageItem> items = fileStorageService.listObjects(bucket, "");

        int deletedCount = 0;
        for (StorageItem item : items) {
            if (item.lastModified() == null || item.lastModified().isAfter(threshold)) {
                continue;
            }

            String key = item.objectKey();
            boolean isUsed = false;

            if (key.startsWith("posters/") || key.startsWith("backdrops/")) {
                isUsed = movieJpaRepository.existsByPosterUrl(key) || movieJpaRepository.existsByBackdropUrl(key);
            } else if (key.startsWith("thumbnails/")) {
                isUsed = episodeJpaRepository.existsByThumbnailUrl(key);
            } else {
                // Thư mục legacy (movies/{id}/...)
                isUsed = movieJpaRepository.existsByPosterUrl(key)
                        || movieJpaRepository.existsByBackdropUrl(key)
                        || episodeJpaRepository.existsByThumbnailUrl(key);
            }

            if (!isUsed) {
                fileStorageService.delete(bucket, key);
                deletedCount++;
                log.info("Deleted orphan movie media from MinIO: bucket={}, key={}", bucket, key);
            }
        }
        if (deletedCount > 0) {
            log.info("Cleaned up {} orphan files from {}", deletedCount, bucket);
        }
    }

    private void cleanupActorAvatarsBucket(Instant threshold) {
        String bucket = minioProperties.getBucketActor();
        List<StorageItem> items = fileStorageService.listObjects(bucket, "");

        int deletedCount = 0;
        for (StorageItem item : items) {
            if (item.lastModified() == null || item.lastModified().isAfter(threshold)) {
                continue;
            }

            String key = item.objectKey();
            boolean isUsed = actorJpaRepository.existsByAvatarUrl(key);

            if (!isUsed) {
                fileStorageService.delete(bucket, key);
                deletedCount++;
                log.info("Deleted orphan actor avatar from MinIO: bucket={}, key={}", bucket, key);
            }
        }
        if (deletedCount > 0) {
            log.info("Cleaned up {} orphan files from {}", deletedCount, bucket);
        }
    }

    private void cleanupUserAvatarsBucket(Instant threshold) {
        String bucket = minioProperties.getBucketAvatar();
        List<StorageItem> items = fileStorageService.listObjects(bucket, "");

        int deletedCount = 0;
        for (StorageItem item : items) {
            if (item.lastModified() == null || item.lastModified().isAfter(threshold)) {
                continue;
            }

            String key = item.objectKey();
            boolean isUsed = userJpaRepository.existsByProfileUrl(key);

            if (!isUsed) {
                fileStorageService.delete(bucket, key);
                deletedCount++;
                log.info("Deleted orphan user avatar from MinIO: bucket={}, key={}", bucket, key);
            }
        }
        if (deletedCount > 0) {
            log.info("Cleaned up {} orphan files from {}", deletedCount, bucket);
        }
    }
}

