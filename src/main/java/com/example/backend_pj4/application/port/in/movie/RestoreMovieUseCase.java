package com.example.backend_pj4.application.port.in.movie;

import com.example.backend_pj4.application.dto.movie.MovieResult;

public interface RestoreMovieUseCase {
    MovieResult execute(String id);
}
