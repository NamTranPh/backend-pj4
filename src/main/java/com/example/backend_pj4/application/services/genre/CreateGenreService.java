package com.example.backend_pj4.application.services.genre;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.backend_pj4.application.dto.request.genre.RequestCreateGenreDto;
import com.example.backend_pj4.common.base.BaseService;
import com.example.backend_pj4.domain.entities.Genre;
import com.example.backend_pj4.domain.repository.GenreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateGenreService extends BaseService {

    private final GenreRepository genreRepository;

    public Genre execute(RequestCreateGenreDto request) {
        Genre genre = Genre.builder()
                .name(request.getName())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return genreRepository.save(genre);
    }
}