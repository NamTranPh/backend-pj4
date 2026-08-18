package com.example.backend_pj4.application.port.in.genre;

import com.example.backend_pj4.application.dto.genre.GenreResult;

public interface GetGenreByIdUseCase {
    GenreResult execute(String id);
}
