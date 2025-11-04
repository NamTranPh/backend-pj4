package com.example.backend_pj4.application.services.movie;

import org.springframework.stereotype.Service;

import com.example.backend_pj4.common.base.BaseService;
import com.example.backend_pj4.domain.repository.MovieRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeleteMovieService extends BaseService {

    private final MovieRepository movieRepository;

    public void execute(String movieId) {
        if (movieRepository.findById(movieId).isEmpty()) {
            throw new EntityNotFoundException("movie not found with id: " + movieId);
        }
        movieRepository.deleteById(movieId);
    }
}