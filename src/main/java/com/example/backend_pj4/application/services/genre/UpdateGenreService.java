package com.example.backend_pj4.application.services.genre;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.request.genre.RequestUpdateGenreDto;
import com.example.backend_pj4.common.base.BaseService;
import com.example.backend_pj4.domain.entities.Genre;
import com.example.backend_pj4.domain.repository.GenreRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateGenreService extends BaseService {

    private final GenreRepository genreRepository;

    @Transactional
    public Genre execute(String genreId, RequestUpdateGenreDto dto) {
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new EntityNotFoundException("Genre not found with id: " + genreId));

        if (dto.getName() != null) {
            genre.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            genre.setDescription(dto.getDescription());
        }

        // Update timestamp
        genre.setUpdatedAt(LocalDateTime.now());

        return genreRepository.save(genre);
    }
}
