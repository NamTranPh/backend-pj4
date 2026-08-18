package com.example.backend_pj4.application.usecase.episode;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.port.in.episode.DeleteEpisodeUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Episode;
import com.example.backend_pj4.domain.repository.EpisodeRepository;

@Service
public class DeleteEpisodeService implements DeleteEpisodeUseCase {

    private final EpisodeRepository episodeRepository;

    public DeleteEpisodeService(EpisodeRepository episodeRepository) {
        this.episodeRepository = episodeRepository;
    }

    @Override
    @Transactional
    public void execute(String movieId, String episodeId) {
        Episode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new CustomException(ErrorCode.EPISODE_NOT_FOUND));

        if (!episode.getMovieId().equals(movieId)) {
            throw new CustomException(ErrorCode.EPISODE_NOT_FOUND);
        }

        Episode deleted = episode.toBuilder()
                .deletedAt(LocalDateTime.now())
                .build();
        episodeRepository.save(deleted);
    }
}
