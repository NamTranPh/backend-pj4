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

@Service
public class DeleteMovieService implements DeleteMovieUseCase {

    private final MovieRepository movieRepository;

    public DeleteMovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    @Transactional
    public void execute(String id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        if (movie.getMovieType() == MovieType.SERIES) {
            movieRepository.softDeleteEpisodesByMovieId(id);
        }

        Movie deleted = movie.toBuilder()
                .deletedAt(LocalDateTime.now())
                .build();
        movieRepository.save(deleted);
    }
}
