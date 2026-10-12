package com.example.backend_pj4.application.port.in.streaming;

import java.io.InputStream;

/**
 * Phát HLS: xác thực token phát, tra khoá object và lấy nội dung từ object storage.
 *
 * <p>Trước đây toàn bộ phần này nằm trong {@code StreamingController} — controller tự gọi
 * {@code MovieRepository}, {@code EpisodeRepository} và {@code FileStorageService}, nên không
 * unit test được và không có chỗ đặt transaction (AGENTS.md §7).
 *
 * <p>Việc viết lại URL trong playlist <b>không</b> thuộc đây: đó là ghép route HTTP
 * ({@code /api/v1/stream/...}), thuần tuý chuyện của presentation.
 */
public interface StreamPlaybackUseCase {

    /** Nội dung master playlist dạng text, chưa viết lại URL. */
    String getMasterPlaylist(String contentId, String playbackToken);

    /**
     * Segment hoặc variant playlist. Caller có trách nhiệm đóng stream.
     *
     * @param segmentPath đường dẫn tương đối lấy từ URL — được kiểm path traversal tại đây
     */
    InputStream getSegment(String contentId, String playbackToken, String segmentPath);
}
