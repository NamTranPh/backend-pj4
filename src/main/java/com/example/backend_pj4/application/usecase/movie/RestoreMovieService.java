package com.example.backend_pj4.application.usecase.movie;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.movie.MovieResult;
import com.example.backend_pj4.application.mapper.MovieResultMapper;
import com.example.backend_pj4.application.port.in.movie.RestoreMovieUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.MovieType;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.MovieRepository;

@Service
public class RestoreMovieService implements RestoreMovieUseCase {

    private final MovieRepository movieRepository;
    private final MovieResultMapper movieResultMapper;

    public RestoreMovieService(MovieRepository movieRepository, MovieResultMapper movieResultMapper) {
        this.movieRepository = movieRepository;
        this.movieResultMapper = movieResultMapper;
    }

    @Override
    @Transactional
    public MovieResult execute(String id) {
        Movie movie = movieRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        if (movie.getDeletedAt() == null) {
            throw new CustomException(ErrorCode.MOVIE_NOT_DELETED);
        }

        if (movie.getMovieType() == MovieType.SERIES) {
            movieRepository.restoreEpisodesByMovieId(id);
        }

        Movie restored = movie.toBuilder().deletedAt(null).build();
        Movie saved = movieRepository.save(restored);
        return movieResultMapper.toResult(saved);
    }
}
