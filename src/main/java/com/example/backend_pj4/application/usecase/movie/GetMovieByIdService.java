package com.example.backend_pj4.application.usecase.movie;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.episode.EpisodeResult;
import com.example.backend_pj4.application.dto.movie.MovieDetailResult;
import com.example.backend_pj4.application.mapper.EpisodeResultMapper;
import com.example.backend_pj4.application.mapper.MovieResultMapper;
import com.example.backend_pj4.application.port.in.movie.GetMovieByIdUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.MovieType;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.EpisodeRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;

@Service
public class GetMovieByIdService implements GetMovieByIdUseCase {

    private final MovieRepository movieRepository;
    private final EpisodeRepository episodeRepository;
    private final MovieResultMapper movieResultMapper;
    private final EpisodeResultMapper episodeResultMapper;

    public GetMovieByIdService(MovieRepository movieRepository, EpisodeRepository episodeRepository, MovieResultMapper movieResultMapper, EpisodeResultMapper episodeResultMapper) {
        this.movieRepository = movieRepository;
        this.episodeRepository = episodeRepository;
        this.movieResultMapper = movieResultMapper;
        this.episodeResultMapper = episodeResultMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public MovieDetailResult execute(String identifier) {
        Movie movie = movieRepository.findBySlug(identifier)
                .or(() -> movieRepository.findById(identifier))
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        List<EpisodeResult> episodes = Collections.emptyList();
        if (movie.getMovieType() == MovieType.SERIES) {
            episodes = episodeRepository.findByMovieIdOrderByEpisodeNumber(movie.getId())
                    .stream()
                    .map(episodeResultMapper::toResult)
                    .toList();
        }

        return movieResultMapper.toDetailResult(movie, episodes);
    }
}
