package com.example.backend_pj4.application.port.in.movie;

import com.example.backend_pj4.application.dto.movie.MovieDetailResult;

public interface GetMovieByIdUseCase {
    default MovieDetailResult execute(String id) {
        return execute(id, null);
    }

    MovieDetailResult execute(String id, String userId);
}
