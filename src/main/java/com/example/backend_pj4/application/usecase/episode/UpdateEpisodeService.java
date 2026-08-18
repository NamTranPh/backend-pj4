package com.example.backend_pj4.application.usecase.episode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.episode.UpdateEpisodeCommand;
import com.example.backend_pj4.application.dto.episode.EpisodeResult;
import com.example.backend_pj4.application.mapper.EpisodeResultMapper;
import com.example.backend_pj4.application.port.in.episode.UpdateEpisodeUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Episode;
import com.example.backend_pj4.domain.repository.EpisodeRepository;

@Service
public class UpdateEpisodeService implements UpdateEpisodeUseCase {

    private final EpisodeRepository episodeRepository;

    public UpdateEpisodeService(EpisodeRepository episodeRepository) {
        this.episodeRepository = episodeRepository;
    }

    @Override
    @Transactional
    public EpisodeResult execute(UpdateEpisodeCommand command) {
        Episode episode = episodeRepository.findById(command.id())
                .orElseThrow(() -> new CustomException(ErrorCode.EPISODE_NOT_FOUND));

        if (!episode.getMovieId().equals(command.movieId())) {
            throw new CustomException(ErrorCode.EPISODE_NOT_FOUND);
        }

        Episode.EpisodeBuilder builder = episode.toBuilder();

        if (command.episodeNumber() != null && !command.episodeNumber().equals(episode.getEpisodeNumber())) {
            episodeRepository.findByMovieIdAndEpisodeNumber(command.movieId(), command.episodeNumber())
                    .ifPresent(e -> { throw new CustomException(ErrorCode.EPISODE_NUMBER_ALREADY_EXISTS); });
            builder.episodeNumber(command.episodeNumber());
        }

        if (command.title() != null) {
            builder.title(command.title());
        }
        if (command.description() != null) {
            builder.description(command.description());
        }
        if (command.duration() != null) {
            builder.duration(command.duration());
        }
        if (command.airDate() != null) {
            builder.airDate(command.airDate());
        }

        Episode saved = episodeRepository.save(builder.build());
        return EpisodeResultMapper.toResult(saved);
    }
}
