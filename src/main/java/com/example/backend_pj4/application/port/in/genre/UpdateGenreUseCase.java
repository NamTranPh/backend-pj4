package com.example.backend_pj4.application.port.in.genre;

import com.example.backend_pj4.application.command.genre.UpdateGenreCommand;
import com.example.backend_pj4.application.dto.genre.GenreResult;

public interface UpdateGenreUseCase {
    GenreResult execute(UpdateGenreCommand command);
}
