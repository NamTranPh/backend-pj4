package com.example.backend_pj4.application.usecase.streaming;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.backend_pj4.application.port.out.VideoTranscoder;
import com.example.backend_pj4.application.port.out.VideoTranscoder.TranscodeCommand;
import com.example.backend_pj4.application.port.out.VideoTranscoder.TranscodeResult;
import com.example.backend_pj4.common.constants.enums.VideoStatus;
import com.example.backend_pj4.domain.model.Episode;
import com.example.backend_pj4.domain.repository.EpisodeRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TranscodeEpisodeService {

    private static final String RAW_BUCKET = "movie-raw";
    private static final String PROCESSED_BUCKET = "movie-processed";

    private final EpisodeRepository episodeRepository;
    private final VideoTranscoder videoTranscoder;

    public TranscodeEpisodeService(EpisodeRepository episodeRepository, VideoTranscoder videoTranscoder) {
        this.episodeRepository = episodeRepository;
        this.videoTranscoder = videoTranscoder;
    }

    @Async("transcodeExecutor")
    public void transcodeAsync(String episodeId, String rawFileKey) {
        try {
            log.info("Starting transcode for episode={}", episodeId);

            Episode episode = episodeRepository.findById(episodeId).orElse(null);
            if (episode == null) {
                log.warn("Episode not found: {}", episodeId);
                return;
            }

            episodeRepository.save(episode.toBuilder().status(VideoStatus.PROCESSING).build());

            String outputPrefix = "episodes/" + episodeId;

            TranscodeResult result = videoTranscoder.transcode(
                    new TranscodeCommand(episodeId, RAW_BUCKET, rawFileKey, PROCESSED_BUCKET, outputPrefix));

            episode = episodeRepository.findById(episodeId).orElse(null);
            if (episode == null) {
                log.warn("Episode not found after transcode: {}", episodeId);
                return;
            }

            episodeRepository.save(episode.toBuilder()
                    .status(VideoStatus.READY)
                    .masterPlaylistKey(result.masterPlaylistKey())
                    .resolutions(result.resolutions())
                    .duration(result.durationSeconds())
                    .build());

            log.info("Transcode completed for episode={}", episodeId);

        } catch (Exception e) {
            log.error("Transcode failed for episode={}", episodeId, e);
            try {
                episodeRepository.findById(episodeId).ifPresent(ep ->
                        episodeRepository.save(ep.toBuilder().status(VideoStatus.FAILED).build()));
            } catch (Exception ex) {
                log.error("Failed to update episode status to FAILED: {}", episodeId, ex);
            }
        }
    }
}
