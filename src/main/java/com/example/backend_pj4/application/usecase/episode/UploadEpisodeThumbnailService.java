package com.example.backend_pj4.application.usecase.episode;

import java.io.InputStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.episode.EpisodeResult;
import com.example.backend_pj4.application.mapper.EpisodeResultMapper;
import com.example.backend_pj4.application.port.in.episode.UploadEpisodeThumbnailUseCase;
import com.example.backend_pj4.application.port.out.FileStorageService;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Episode;
import com.example.backend_pj4.domain.repository.EpisodeRepository;

@Service
public class UploadEpisodeThumbnailService implements UploadEpisodeThumbnailUseCase {

    private static final String BUCKET = "movie-public";
    private final EpisodeRepository episodeRepository;
    private final FileStorageService fileStorageService;

    public UploadEpisodeThumbnailService(EpisodeRepository episodeRepository, FileStorageService fileStorageService) {
        this.episodeRepository = episodeRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional
    public EpisodeResult execute(String movieId, String episodeId, String filename, InputStream data, long size, String contentType) {
        Episode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new CustomException(ErrorCode.EPISODE_NOT_FOUND));

        if (!episode.getMovieId().equals(movieId)) {
            throw new CustomException(ErrorCode.EPISODE_NOT_FOUND);
        }

        if (!contentType.startsWith("image/")) {
            throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
        }

        String folder = "movies/" + movieId + "/episodes/" + episodeId;
        String ext = filename.contains(".") ? filename.substring(filename.lastIndexOf(".")) : ".jpg";
        String objectKey = fileStorageService.upload(BUCKET, folder, "thumbnail" + ext, data, size, contentType);
        String url = fileStorageService.getPublicUrl(BUCKET, objectKey);

        Episode updated = episode.toBuilder().thumbnailUrl(url).build();
        Episode saved = episodeRepository.save(updated);
        return EpisodeResultMapper.toResult(saved);
    }
}
