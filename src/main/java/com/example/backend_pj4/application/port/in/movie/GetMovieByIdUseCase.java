package com.example.backend_pj4.application.port.in.movie;

import com.example.backend_pj4.application.dto.movie.MovieDetailResult;

public interface GetMovieByIdUseCase {
    MovieDetailResult execute(String id);
}
