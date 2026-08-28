package com.example.backend_pj4.application.usecase.episode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.episode.EpisodeResult;
import com.example.backend_pj4.application.mapper.EpisodeResultMapper;
import com.example.backend_pj4.application.port.in.episode.PublishEpisodeUseCase;
import com.example.backend_pj4.application.usecase.streaming.TranscodeEpisodeService;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.VideoStatus;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Episode;
import com.example.backend_pj4.domain.repository.EpisodeRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PublishEpisodeService implements PublishEpisodeUseCase {

    private final EpisodeRepository episodeRepository;
    private final TranscodeEpisodeService transcodeEpisodeService;
    private final EpisodeResultMapper episodeResultMapper;

    public PublishEpisodeService(
            EpisodeRepository episodeRepository,
            TranscodeEpisodeService transcodeEpisodeService,
            EpisodeResultMapper episodeResultMapper
    ) {
        this.episodeRepository = episodeRepository;
        this.transcodeEpisodeService = transcodeEpisodeService;
        this.episodeResultMapper = episodeResultMapper;
    }

    @Override
    @Transactional
    public EpisodeResult execute(String episodeId) {
        Episode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new CustomException(ErrorCode.EPISODE_NOT_FOUND));

        if (episode.getStatus() == VideoStatus.PROCESSING) {
            throw new CustomException(ErrorCode.EPISODE_ALREADY_PROCESSING);
        }

        if (episode.getRawFileKey() == null || episode.getRawFileKey().isBlank()) {
            throw new CustomException(ErrorCode.EPISODE_NO_VIDEO);
        }

        if (episode.getMasterPlaylistKey() != null && !episode.getMasterPlaylistKey().isBlank()) {
            Episode ready = episodeRepository.save(episode.toBuilder()
                    .status(VideoStatus.READY)
                    .build());
            log.info("Episode published (already transcoded) episodeId={}", episodeId);
            return episodeResultMapper.toResult(ready);
        }

        Episode processing = episodeRepository.save(episode.toBuilder()
                .status(VideoStatus.PROCESSING)
                .build());

        transcodeEpisodeService.transcodeAsync(episodeId, episode.getRawFileKey());
        log.info("Episode publish triggered transcode episodeId={}", episodeId);
        return episodeResultMapper.toResult(processing);
    }
}
