package com.example.backend_pj4.application.usecase.movie;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.port.in.movie.DeleteMovieUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.MovieType;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.infrastructure.database.repositories.MovieJpaRepository;

@Service
public class DeleteMovieService implements DeleteMovieUseCase {

    private final MovieRepository movieRepository;
    private final MovieJpaRepository movieJpaRepository;

    public DeleteMovieService(MovieRepository movieRepository, MovieJpaRepository movieJpaRepository) {
        this.movieRepository = movieRepository;
        this.movieJpaRepository = movieJpaRepository;
    }

    @Override
    @Transactional
    public void execute(String id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        if (movie.getMovieType() == MovieType.SERIES) {
            movieJpaRepository.softDeleteEpisodesByMovieId(id);
        }

        Movie deleted = movie.toBuilder()
                .deletedAt(LocalDateTime.now())
                .build();
        movieRepository.save(deleted);
    }
}
