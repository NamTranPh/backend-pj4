package com.example.backend_pj4.application.port.in.movie;

import com.example.backend_pj4.application.command.movie.CreateMovieCommand;
import com.example.backend_pj4.application.dto.movie.MovieResult;

public interface CreateMovieUseCase {
    MovieResult execute(CreateMovieCommand command);
}
