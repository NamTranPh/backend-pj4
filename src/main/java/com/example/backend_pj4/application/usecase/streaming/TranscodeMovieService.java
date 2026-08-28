package com.example.backend_pj4.application.usecase.streaming;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.port.out.VideoTranscoder;
import com.example.backend_pj4.application.port.out.VideoTranscoder.TranscodeCommand;
import com.example.backend_pj4.application.port.out.VideoTranscoder.TranscodeResult;
import com.example.backend_pj4.common.constants.enums.VideoStatus;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.MovieRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TranscodeMovieService {

    private static final String RAW_BUCKET = "movie-raw";
    private static final String PROCESSED_BUCKET = "movie-processed";

    private final MovieRepository movieRepository;
    private final VideoTranscoder videoTranscoder;

    public TranscodeMovieService(MovieRepository movieRepository, VideoTranscoder videoTranscoder) {
        this.movieRepository = movieRepository;
        this.videoTranscoder = videoTranscoder;
    }

    @Async("transcodeExecutor")
    @Transactional
    public void transcodeAsync(String movieId, String rawFileKey) {
        try {
            log.info("Starting transcode for movie={}", movieId);

            Movie movie = movieRepository.findById(movieId).orElse(null);
            if (movie == null) {
                log.warn("Movie not found: {}", movieId);
                return;
            }

            movieRepository.save(movie.toBuilder().status(VideoStatus.PROCESSING).build());

            String outputPrefix = "movies/" + movieId;

            TranscodeResult result = videoTranscoder.transcode(
                    new TranscodeCommand(movieId, RAW_BUCKET, rawFileKey, PROCESSED_BUCKET, outputPrefix));

            movie = movieRepository.findById(movieId).orElse(null);
            if (movie == null) {
                log.warn("Movie not found after transcode: {}", movieId);
                return;
            }

            movieRepository.save(movie.toBuilder()
                    .status(VideoStatus.READY)
                    .masterPlaylistKey(result.masterPlaylistKey())
                    .resolutions(result.resolutions())
                    .duration(result.durationSeconds())
                    .build());

            log.info("Transcode completed for movie={}", movieId);

        } catch (Exception e) {
            log.error("Transcode failed for movie={}", movieId, e);
            try {
                movieRepository.findById(movieId).ifPresent(m ->
                        movieRepository.save(m.toBuilder().status(VideoStatus.FAILED).build()));
            } catch (Exception ex) {
                log.error("Failed to update movie status to FAILED: {}", movieId, ex);
            }
        }
    }
}
