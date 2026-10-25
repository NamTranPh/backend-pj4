package com.example.backend_pj4.application.usecase.actor;

import java.io.InputStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.actor.ActorResult;
import com.example.backend_pj4.application.mapper.ActorResultMapper;
import com.example.backend_pj4.application.port.in.actor.UploadActorAvatarUseCase;
import com.example.backend_pj4.application.port.out.FileStorageService;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Actor;
import com.example.backend_pj4.domain.repository.ActorRepository;
import com.example.backend_pj4.infrastructure.config.properties.MinioProperties;

@Service
public class UploadActorAvatarService implements UploadActorAvatarUseCase {

    private final ActorRepository actorRepository;
    private final FileStorageService fileStorageService;
    private final MinioProperties minioProperties;
    private final ActorResultMapper actorResultMapper;

    public UploadActorAvatarService(ActorRepository actorRepository,
                                   FileStorageService fileStorageService,
                                   MinioProperties minioProperties,
                                   ActorResultMapper actorResultMapper) {
        this.actorRepository = actorRepository;
        this.fileStorageService = fileStorageService;
        this.minioProperties = minioProperties;
        this.actorResultMapper = actorResultMapper;
    }

    @Override
    @Transactional
    public ActorResult execute(String actorId, String filename, InputStream data, long size, String contentType) {
        Actor actor = actorRepository.findById(actorId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACTOR_NOT_FOUND));

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
        }

        String bucket = minioProperties.getBucketActor();
        String folder = "actors/" + actorId;
        String ext = (filename != null && filename.contains("."))
                ? filename.substring(filename.lastIndexOf("."))
                : ".jpg";

        String objectKey = fileStorageService.upload(bucket, folder, "avatar" + ext, data, size, contentType);

        Actor updated = actor.toBuilder()
                .avatarUrl(objectKey)
                .build();

        Actor saved = actorRepository.save(updated);
        return actorResultMapper.toResult(saved);
    }
}
