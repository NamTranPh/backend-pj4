package com.example.backend_pj4.application.port.in.movie;

import com.example.backend_pj4.application.command.movie.UpdateMovieCommand;
import com.example.backend_pj4.application.dto.movie.MovieResult;

public interface UpdateMovieUseCase {
    MovieResult execute(UpdateMovieCommand command);
}
