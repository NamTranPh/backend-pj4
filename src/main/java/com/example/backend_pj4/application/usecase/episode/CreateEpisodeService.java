package com.example.backend_pj4.application.usecase.episode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.episode.CreateEpisodeCommand;
import com.example.backend_pj4.application.dto.episode.EpisodeResult;
import com.example.backend_pj4.application.mapper.EpisodeResultMapper;
import com.example.backend_pj4.application.port.in.episode.CreateEpisodeUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.MovieType;
import com.example.backend_pj4.common.constants.enums.VideoStatus;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Episode;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.EpisodeRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;

@Service
public class CreateEpisodeService implements CreateEpisodeUseCase {

    private final MovieRepository movieRepository;
    private final EpisodeRepository episodeRepository;

    public CreateEpisodeService(MovieRepository movieRepository, EpisodeRepository episodeRepository) {
        this.movieRepository = movieRepository;
        this.episodeRepository = episodeRepository;
    }

    @Override
    @Transactional
    public EpisodeResult execute(CreateEpisodeCommand command) {
        Movie movie = movieRepository.findById(command.movieId())
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        if (movie.getMovieType() != MovieType.SERIES) {
            throw new CustomException(ErrorCode.MOVIE_NOT_SERIES);
        }

        episodeRepository.findByMovieIdAndEpisodeNumber(command.movieId(), command.episodeNumber())
                .ifPresent(e -> { throw new CustomException(ErrorCode.EPISODE_NUMBER_ALREADY_EXISTS); });

        Episode episode = Episode.builder()
                .movieId(command.movieId())
                .episodeNumber(command.episodeNumber())
                .title(command.title())
                .description(command.description())
                .duration(command.duration())
                .airDate(command.airDate())
                .status(VideoStatus.DRAFT)
                .isPremium(false)
                .viewCount(0L)
                .build();

        Episode saved = episodeRepository.save(episode);
        return EpisodeResultMapper.toResult(saved);
    }
}
