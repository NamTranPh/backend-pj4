package com.example.backend_pj4.presentation.streaming;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
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

import com.example.backend_pj4.application.port.in.streaming.StreamPlaybackUseCase;
import com.example.backend_pj4.common.annotation.IgnoreResponseWrapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "Streaming")
@IgnoreResponseWrapping
@RestController
@RequestMapping("/api/v1/stream")
public class StreamingController {

    private static final MediaType HLS_MEDIA_TYPE = MediaType.parseMediaType("application/vnd.apple.mpegurl");
    private static final String SEGMENTS_PREFIX = "/segments/";

    private final StreamPlaybackUseCase streamPlaybackUseCase;

    public StreamingController(StreamPlaybackUseCase streamPlaybackUseCase) {
        this.streamPlaybackUseCase = streamPlaybackUseCase;
    }

    @Operation(summary = "Lấy master playlist HLS.")
    @GetMapping("/{contentId}/master.m3u8")
    public ResponseEntity<String> masterPlaylist(
            @PathVariable String contentId,
            @RequestParam String token
    ) {
        String playbackToken = sanitizeToken(token);
        String playlist = streamPlaybackUseCase.getMasterPlaylist(contentId, playbackToken);

        return ResponseEntity.ok()
                .contentType(HLS_MEDIA_TYPE)
                .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                .body(rewritePlaylistUrls(playlist, contentId, playbackToken, ""));
    }

    @Operation(summary = "Proxy HLS segment hoặc variant playlist.")
    @GetMapping("/{contentId}/segments/**")
    public ResponseEntity<InputStreamResource> segment(
            @PathVariable String contentId,
            @RequestParam String token,
            HttpServletRequest request
    ) {
        String playbackToken = sanitizeToken(token);
        String segmentPath = extractSegmentPath(request);

        InputStream is = streamPlaybackUseCase.getSegment(contentId, playbackToken, segmentPath);

        if (!segmentPath.endsWith(".m3u8")) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CACHE_CONTROL, "max-age=86400")
                    .body(new InputStreamResource(is));
        }

        // Variant playlist cũng chứa URL tương đối, phải viết lại y như master.
        int prefixSlash = segmentPath.lastIndexOf('/');
        String prefix = prefixSlash >= 0 ? segmentPath.substring(0, prefixSlash + 1) : "";
        String rewritten = rewritePlaylistUrls(readAll(is), contentId, playbackToken, prefix);

        return ResponseEntity.ok()
                .contentType(HLS_MEDIA_TYPE)
                .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                .body(new InputStreamResource(
                        new ByteArrayInputStream(rewritten.getBytes(StandardCharsets.UTF_8))));
    }

    /** {@code /segments/**} không bind được bằng {@code @PathVariable}, phải cắt từ URI. */
    private String extractSegmentPath(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.substring(uri.indexOf(SEGMENTS_PREFIX) + SEGMENTS_PREFIX.length());
    }

    /** Một số player nối hai giá trị {@code ?token=} thành "a,b" — chỉ lấy phần đầu. */
    private String sanitizeToken(String token) {
        int comma = token.indexOf(',');
        return comma > 0 ? token.substring(0, comma) : token;
    }

    private String readAll(InputStream is) {
        try (InputStream in = is) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Không đọc được variant playlist", e);
        }
    }

    /**
     * Đổi URL tương đối trong playlist thành route proxy có kèm token.
     *
     * <p>Ở lại presentation: đây là ghép đường dẫn HTTP của chính controller này, không phải
     * luật nghiệp vụ.
     */
    private String rewritePlaylistUrls(String content, String contentId, String token, String segmentPrefix) {
        StringBuilder result = new StringBuilder();
        for (String line : content.split("\n")) {
            if (!line.startsWith("#") && !line.isBlank()) {
                result.append("/api/v1/stream/")
                        .append(contentId)
                        .append(SEGMENTS_PREFIX)
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
