package com.example.backend_pj4.application.port.in.genre;

import com.example.backend_pj4.application.command.genre.CreateGenreCommand;
import com.example.backend_pj4.application.dto.genre.GenreResult;

public interface CreateGenreUseCase {
    GenreResult execute(CreateGenreCommand command);
}
