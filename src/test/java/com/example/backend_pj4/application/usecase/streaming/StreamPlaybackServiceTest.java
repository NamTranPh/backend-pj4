package com.example.backend_pj4.application.usecase.streaming;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.backend_pj4.application.port.out.FileStorageService;
import com.example.backend_pj4.application.port.out.PlaybackTokenService;
import com.example.backend_pj4.application.port.out.PlaybackTokenService.PlaybackTokenPayload;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.EpisodeRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.infrastructure.config.properties.MinioProperties;

/**
 * Logic này trước nằm trong {@code StreamingController} nên không viết được test kiểu này —
 * phải dựng {@code @WebMvcTest} mới chạm tới được.
 */
class StreamPlaybackServiceTest {

    private static final String CONTENT_ID = "mov_1";
    private static final String TOKEN = "tok";
    private static final String MASTER_KEY = "movies/mov_1/hls/master.m3u8";

    private PlaybackTokenService playbackTokenService;
    private FileStorageService fileStorageService;
    private MovieRepository movieRepository;
    private StreamPlaybackService service;

    @BeforeEach
    void setUp() {
        playbackTokenService = mock(PlaybackTokenService.class);
        fileStorageService = mock(FileStorageService.class);
        movieRepository = mock(MovieRepository.class);
        EpisodeRepository episodeRepository = mock(EpisodeRepository.class);

        MinioProperties minioProperties = new MinioProperties();
        minioProperties.setBucketProcessed("processed");

        service = new StreamPlaybackService(playbackTokenService, fileStorageService,
                movieRepository, episodeRepository, minioProperties);
    }

    private void givenValidTokenFor(String contentIdInToken) {
        when(playbackTokenService.verifyToken(TOKEN))
                .thenReturn(new PlaybackTokenPayload(contentIdInToken, "MOVIE", "usr_1"));
        when(movieRepository.findById(anyString()))
                .thenReturn(Optional.of(Movie.builder().id(CONTENT_ID).masterPlaylistKey(MASTER_KEY).build()));
        when(fileStorageService.getObject(anyString(), anyString()))
                .thenReturn(new ByteArrayInputStream("#EXTM3U\n".getBytes(StandardCharsets.UTF_8)));
    }

    private ErrorCode errorOf(Runnable action) {
        try {
            action.run();
            throw new AssertionError("mong đợi CustomException nhưng không có lỗi nào");
        } catch (CustomException e) {
            return e.getErrorCode();
        }
    }

    @Test
    void tokenHopLeDungNoiDung_thiDocDuocMasterPlaylist() {
        givenValidTokenFor(CONTENT_ID);

        assertThat(service.getMasterPlaylist(CONTENT_ID, TOKEN)).startsWith("#EXTM3U");
        verify(fileStorageService).getObject("processed", MASTER_KEY);
    }

    @Test
    void tokenCuaNoiDungKHAC_thiTuChoi() {
        givenValidTokenFor("mov_KHAC");   // token hợp lệ, nhưng phát hành cho phim khác

        assertThat(errorOf(() -> service.getMasterPlaylist(CONTENT_ID, TOKEN)))
                .as("không đối chiếu thì một token phim miễn phí mở được mọi phim trả phí")
                .isEqualTo(ErrorCode.PLAYBACK_TOKEN_INVALID);
    }

    @Test
    void segmentPathCoDauChamChamDoi_thiChanTruocKhiChamStorage() {
        givenValidTokenFor(CONTENT_ID);

        assertThat(errorOf(() -> service.getSegment(CONTENT_ID, TOKEN, "../../other/secret.ts")))
                .isEqualTo(ErrorCode.PLAYBACK_TOKEN_INVALID);

        verify(fileStorageService, never()).getObject(anyString(), anyString());
    }

    @Test
    void segmentPathTuyetDoi_cungBiChan() {
        givenValidTokenFor(CONTENT_ID);

        assertThat(errorOf(() -> service.getSegment(CONTENT_ID, TOKEN, "/etc/passwd")))
                .isEqualTo(ErrorCode.PLAYBACK_TOKEN_INVALID);
    }

    @Test
    void segmentHopLe_duocGhepVaoDUNGThuMucCuaNoiDung() {
        givenValidTokenFor(CONTENT_ID);

        service.getSegment(CONTENT_ID, TOKEN, "720p/seg_001.ts");

        verify(fileStorageService).getObject(eq("processed"), eq("movies/mov_1/hls/720p/seg_001.ts"));
    }

    @Test
    void phimDaXoa_traMOVIE_NOT_FOUND() {
        when(playbackTokenService.verifyToken(TOKEN))
                .thenReturn(new PlaybackTokenPayload(CONTENT_ID, "MOVIE", "usr_1"));
        when(movieRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThat(errorOf(() -> service.getMasterPlaylist(CONTENT_ID, TOKEN)))
                .isEqualTo(ErrorCode.MOVIE_NOT_FOUND);
    }
}
