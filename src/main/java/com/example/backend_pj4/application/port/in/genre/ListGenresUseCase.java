package com.example.backend_pj4.application.port.in.genre;

import org.springframework.data.domain.Page;

import com.example.backend_pj4.application.dto.genre.GenreResult;

public interface ListGenresUseCase {
    Page<GenreResult> execute(String search, int page, int size, boolean activeOnly);
}
