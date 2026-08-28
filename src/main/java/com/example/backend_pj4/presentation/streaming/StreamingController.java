package com.example.backend_pj4.presentation.streaming;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.port.out.FileStorageService;
import com.example.backend_pj4.application.port.out.PlaybackTokenService;
import com.example.backend_pj4.application.port.out.PlaybackTokenService.PlaybackTokenPayload;
import com.example.backend_pj4.common.annotation.IgnoreResponseWrapping;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Episode;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.EpisodeRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.infrastructure.config.properties.MinioProperties;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Streaming")
@IgnoreResponseWrapping
@RestController
@RequestMapping("/api/v1/stream")
public class StreamingController {

    private static final MediaType HLS_MEDIA_TYPE = MediaType.parseMediaType("application/vnd.apple.mpegurl");

    private final PlaybackTokenService playbackTokenService;
    private final FileStorageService fileStorageService;
    private final EpisodeRepository episodeRepository;
    private final MovieRepository movieRepository;
    private final MinioProperties minioProperties;

    public StreamingController(
            PlaybackTokenService playbackTokenService,
            FileStorageService fileStorageService,
            EpisodeRepository episodeRepository,
            MovieRepository movieRepository,
            MinioProperties minioProperties
    ) {
        this.playbackTokenService = playbackTokenService;
        this.fileStorageService = fileStorageService;
        this.episodeRepository = episodeRepository;
        this.movieRepository = movieRepository;
        this.minioProperties = minioProperties;
    }

    @Operation(summary = "Lấy master playlist HLS.")
    @GetMapping("/{contentId}/master.m3u8")
    public ResponseEntity<String> masterPlaylist(
            @PathVariable String contentId,
            @RequestParam String token
    ) {
        token = sanitizeToken(token);
        PlaybackTokenPayload payload = playbackTokenService.verifyToken(token);
        if (!payload.contentId().equals(contentId)) {
            throw new CustomException(ErrorCode.PLAYBACK_TOKEN_INVALID);
        }

        String masterKey = resolveMasterPlaylistKey(payload.contentType(), contentId);

        InputStream is = fileStorageService.getObject(minioProperties.getBucketProcessed(), masterKey);

        try {
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            is.close();

            String rewritten = rewritePlaylistUrls(content, contentId, token, "");

            return ResponseEntity.ok()
                    .contentType(HLS_MEDIA_TYPE)
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                    .body(rewritten);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read master playlist", e);
        }
    }

    @Operation(summary = "Proxy HLS segment hoặc variant playlist.")
    @GetMapping("/{contentId}/segments/**")
    public ResponseEntity<InputStreamResource> segment(
            @PathVariable String contentId,
            @RequestParam String token,
            jakarta.servlet.http.HttpServletRequest request
    ) {
        token = sanitizeToken(token);
        PlaybackTokenPayload payload = playbackTokenService.verifyToken(token);
        if (!payload.contentId().equals(contentId)) {
            throw new CustomException(ErrorCode.PLAYBACK_TOKEN_INVALID);
        }

        String fullPath = request.getRequestURI();
        String segmentPath = fullPath.substring(
                fullPath.indexOf("/segments/") + "/segments/".length());

        if (segmentPath.contains("..") || segmentPath.startsWith("/")) {
            throw new CustomException(ErrorCode.PLAYBACK_TOKEN_INVALID);
        }

        String masterKey = resolveMasterPlaylistKey(payload.contentType(), contentId);
        int lastSlash = masterKey.lastIndexOf('/');
        String baseDir = lastSlash >= 0 ? masterKey.substring(0, lastSlash + 1) : "";
        String objectKey = baseDir + segmentPath;

        InputStream is = fileStorageService.getObject(minioProperties.getBucketProcessed(), objectKey);

        MediaType contentType;
        if (segmentPath.endsWith(".m3u8")) {
            contentType = HLS_MEDIA_TYPE;

            try {
                String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                is.close();
                int prefixSlash = segmentPath.lastIndexOf('/');
                String prefix = prefixSlash >= 0 ? segmentPath.substring(0, prefixSlash + 1) : "";
                String rewritten = rewritePlaylistUrls(content, contentId, token, prefix);
                InputStream rewrittenIs = new java.io.ByteArrayInputStream(
                        rewritten.getBytes(StandardCharsets.UTF_8));
                return ResponseEntity.ok()
                        .contentType(contentType)
                        .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                        .body(new InputStreamResource(rewrittenIs));
            } catch (Exception e) {
                throw new RuntimeException("Failed to read variant playlist", e);
            }
        } else {
            contentType = MediaType.APPLICATION_OCTET_STREAM;
        }

        return ResponseEntity.ok()
                .contentType(contentType)
                .header(HttpHeaders.CACHE_CONTROL, "max-age=86400")
                .body(new InputStreamResource(is));
    }

    private String resolveMasterPlaylistKey(String contentType, String contentId) {
        if ("MOVIE".equals(contentType)) {
            Movie movie = movieRepository.findById(contentId)
                    .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));
            return movie.getMasterPlaylistKey();
        }
        Episode episode = episodeRepository.findById(contentId)
                .orElseThrow(() -> new CustomException(ErrorCode.EPISODE_NOT_FOUND));
        return episode.getMasterPlaylistKey();
    }

    private String sanitizeToken(String token) {
        int comma = token.indexOf(',');
        return comma > 0 ? token.substring(0, comma) : token;
    }

    private String rewritePlaylistUrls(String content, String contentId, String token, String segmentPrefix) {
        StringBuilder result = new StringBuilder();
        for (String line : content.split("\n")) {
            if (!line.startsWith("#") && !line.isBlank()) {
                result.append("/api/v1/stream/")
                        .append(contentId)
                        .append("/segments/")
                        .append(segmentPrefix)
                        .append(line.trim())
                        .append("?token=").append(token);
            } else {
                result.append(line);
            }
            result.append("\n");
        }
        return result.toString();
    }
}
