package com.example.backend_pj4.application.services.genre;

import org.springframework.stereotype.Service;

import com.example.backend_pj4.common.base.BaseService;
import com.example.backend_pj4.domain.repository.GenreRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeleteGenreService extends BaseService {

    private final GenreRepository genreRepository;

    public void execute(String genreId) {
        if (genreRepository.findById(genreId).isEmpty()) {
            throw new EntityNotFoundException("Genre not found with id: " + genreId);
        }
        genreRepository.deleteById(genreId);
    }
}