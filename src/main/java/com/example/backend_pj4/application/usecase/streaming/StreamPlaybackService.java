package com.example.backend_pj4.application.usecase.streaming;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.port.in.streaming.StreamPlaybackUseCase;
import com.example.backend_pj4.application.port.out.FileStorageService;
import com.example.backend_pj4.application.port.out.PlaybackTokenService;
import com.example.backend_pj4.application.port.out.PlaybackTokenService.PlaybackTokenPayload;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.repository.EpisodeRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.infrastructure.config.properties.MinioProperties;

@Service
public class StreamPlaybackService implements StreamPlaybackUseCase {

    private final PlaybackTokenService playbackTokenService;
    private final FileStorageService fileStorageService;
    private final MovieRepository movieRepository;
    private final EpisodeRepository episodeRepository;
    private final MinioProperties minioProperties;

    public StreamPlaybackService(
            PlaybackTokenService playbackTokenService,
            FileStorageService fileStorageService,
            MovieRepository movieRepository,
            EpisodeRepository episodeRepository,
            MinioProperties minioProperties
    ) {
        this.playbackTokenService = playbackTokenService;
        this.fileStorageService = fileStorageService;
        this.movieRepository = movieRepository;
        this.episodeRepository = episodeRepository;
        this.minioProperties = minioProperties;
    }

    @Override
    @Transactional(readOnly = true)
    public String getMasterPlaylist(String contentId, String playbackToken) {
        String masterKey = resolveMasterPlaylistKey(contentId, playbackToken);

        try (InputStream is = openProcessed(masterKey)) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Không đọc được master playlist " + masterKey, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public InputStream getSegment(String contentId, String playbackToken, String segmentPath) {
        // Kiểm trước khi chạm tới storage: segmentPath đến thẳng từ URL, "../" sẽ đọc được
        // object ngoài thư mục của nội dung này.
        if (segmentPath.contains("..") || segmentPath.startsWith("/")) {
            throw new CustomException(ErrorCode.PLAYBACK_TOKEN_INVALID);
        }

        String masterKey = resolveMasterPlaylistKey(contentId, playbackToken);
        int lastSlash = masterKey.lastIndexOf('/');
        String baseDir = lastSlash >= 0 ? masterKey.substring(0, lastSlash + 1) : "";

        return openProcessed(baseDir + segmentPath);
    }

    /**
     * Xác thực token rồi tra khoá object.
     *
     * <p>Token mang sẵn {@code contentId}; phải đối chiếu với {@code contentId} trên URL, nếu
     * không thì một token hợp lệ của phim miễn phí mở được mọi phim trả phí.
     */
    private String resolveMasterPlaylistKey(String contentId, String playbackToken) {
        PlaybackTokenPayload payload = playbackTokenService.verifyToken(playbackToken);
        if (!payload.contentId().equals(contentId)) {
            throw new CustomException(ErrorCode.PLAYBACK_TOKEN_INVALID);
        }

        if ("MOVIE".equals(payload.contentType())) {
            return movieRepository.findById(contentId)
                    .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND))
                    .getMasterPlaylistKey();
        }

        return episodeRepository.findById(contentId)
                .orElseThrow(() -> new CustomException(ErrorCode.EPISODE_NOT_FOUND))
                .getMasterPlaylistKey();
    }

    private InputStream openProcessed(String objectKey) {
        return fileStorageService.getObject(minioProperties.getBucketProcessed(), objectKey);
    }
}
