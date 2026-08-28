package com.example.backend_pj4.application.usecase.movie;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.movie.MovieResult;
import com.example.backend_pj4.application.mapper.MovieResultMapper;
import com.example.backend_pj4.application.port.in.movie.PublishMovieUseCase;
import com.example.backend_pj4.application.usecase.streaming.TranscodeMovieService;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.MovieType;
import com.example.backend_pj4.common.constants.enums.VideoStatus;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.EpisodeRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PublishMovieService implements PublishMovieUseCase {

    private final MovieRepository movieRepository;
    private final EpisodeRepository episodeRepository;
    private final TranscodeMovieService transcodeMovieService;
    private final MovieResultMapper movieResultMapper;

    public PublishMovieService(
            MovieRepository movieRepository,
            EpisodeRepository episodeRepository,
            TranscodeMovieService transcodeMovieService,
            MovieResultMapper movieResultMapper
    ) {
        this.movieRepository = movieRepository;
        this.episodeRepository = episodeRepository;
        this.transcodeMovieService = transcodeMovieService;
        this.movieResultMapper = movieResultMapper;
    }

    @Override
    @Transactional
    public MovieResult execute(String movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        if (movie.getStatus() == VideoStatus.READY) {
            return movieResultMapper.toResult(movie);
        }

        if (movie.getMovieType() == MovieType.SINGLE) {
            return publishSingleMovie(movie);
        } else {
            return publishSeriesMovie(movie);
        }
    }

    private MovieResult publishSingleMovie(Movie movie) {
        if (movie.getRawFileKey() == null || movie.getRawFileKey().isBlank()) {
            throw new CustomException(ErrorCode.MOVIE_NO_VIDEO);
        }

        if (movie.getMasterPlaylistKey() != null && !movie.getMasterPlaylistKey().isBlank()) {
            Movie published = movieRepository.save(movie.toBuilder()
                    .status(VideoStatus.READY)
                    .build());
            log.info("Movie published (already transcoded) movieId={}", movie.getId());
            return movieResultMapper.toResult(published);
        }

        Movie processing = movieRepository.save(movie.toBuilder()
                .status(VideoStatus.PROCESSING)
                .build());

        transcodeMovieService.transcodeAsync(movie.getId(), movie.getRawFileKey());
        log.info("Movie publish triggered transcode movieId={}", movie.getId());
        return movieResultMapper.toResult(processing);
    }

    private MovieResult publishSeriesMovie(Movie movie) {
        long readyEpisodes = episodeRepository.countByMovieIdAndStatus(movie.getId(), VideoStatus.READY);
        if (readyEpisodes == 0) {
            throw new CustomException(ErrorCode.MOVIE_NO_READY_EPISODES);
        }

        Movie published = movieRepository.save(movie.toBuilder()
                .status(VideoStatus.READY)
                .build());
        log.info("Series published movieId={} readyEpisodes={}", movie.getId(), readyEpisodes);
        return movieResultMapper.toResult(published);
    }
}
