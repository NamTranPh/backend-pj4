package com.example.backend_pj4.application.usecase.genre;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.genre.GenreResult;
import com.example.backend_pj4.application.mapper.GenreResultMapper;
import com.example.backend_pj4.application.port.in.genre.ListGenresUseCase;
import com.example.backend_pj4.common.constants.enums.GenreStatus;
import com.example.backend_pj4.domain.repository.GenreRepository;

@Service
public class ListGenresService implements ListGenresUseCase {

    private final GenreRepository genreRepository;

    public ListGenresService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GenreResult> execute(String search, int page, int size, boolean activeOnly) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 50), Sort.by("createdAt").descending());

        if (search != null && !search.isBlank()) {
            if (activeOnly) {
                return genreRepository.findByNameContainingAndStatus(search, GenreStatus.ACTIVE, pageable)
                        .map(GenreResultMapper::toResult);
            }
            return genreRepository.findByNameContaining(search, pageable)
                    .map(GenreResultMapper::toResult);
        }

        if (activeOnly) {
            return genreRepository.findByStatus(GenreStatus.ACTIVE, pageable)
                    .map(GenreResultMapper::toResult);
        }
        return genreRepository.findAll(pageable)
                .map(GenreResultMapper::toResult);
    }
}
