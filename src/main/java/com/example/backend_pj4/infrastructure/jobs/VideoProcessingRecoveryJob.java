package com.example.backend_pj4.infrastructure.jobs;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.common.constants.enums.VideoStatus;
import com.example.backend_pj4.domain.model.Episode;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.EpisodeRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class VideoProcessingRecoveryJob {

    private final MovieRepository movieRepository;
    private final EpisodeRepository episodeRepository;

    public VideoProcessingRecoveryJob(MovieRepository movieRepository, EpisodeRepository episodeRepository) {
        this.movieRepository = movieRepository;
        this.episodeRepository = episodeRepository;
    }

    /**
     * Chạy định kỳ 15 phút để quét các phim hoặc tập phim bị treo ở PROCESSING > 45 phút
     * (thường do server restart hoặc FFmpeg process bị crash ngang lúc @Async đang chạy).
     * Chuyển trạng thái về FAILED để Admin có thể kiểm tra và chủ động bấm "Xuất bản" lại.
     */
    @Scheduled(fixedDelay = 900000, initialDelay = 60000)
    @Transactional
    public void recoverStuckProcessingVideos() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(45);

        List<Movie> stuckMovies = movieRepository.findStuckProcessingMovies(threshold);
        if (!stuckMovies.isEmpty()) {
            log.warn("Found {} stuck PROCESSING movies older than 45m, marking as FAILED", stuckMovies.size());
            for (Movie movie : stuckMovies) {
                movieRepository.save(movie.toBuilder().status(VideoStatus.FAILED).build());
                log.info("Recovered stuck movie: id={}, title={}", movie.getId(), movie.getTitle());
            }
        }

        List<Episode> stuckEpisodes = episodeRepository.findStuckProcessingEpisodes(threshold);
        if (!stuckEpisodes.isEmpty()) {
            log.warn("Found {} stuck PROCESSING episodes older than 45m, marking as FAILED", stuckEpisodes.size());
            for (Episode episode : stuckEpisodes) {
                episodeRepository.save(episode.toBuilder().status(VideoStatus.FAILED).build());
                log.info("Recovered stuck episode: id={}, title={}, epNumber={}",
                        episode.getId(), episode.getTitle(), episode.getEpisodeNumber());
            }
        }
    }
}
