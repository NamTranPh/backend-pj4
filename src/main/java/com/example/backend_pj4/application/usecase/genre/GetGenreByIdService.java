package com.example.backend_pj4.application.usecase.genre;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.genre.GenreResult;
import com.example.backend_pj4.application.mapper.GenreResultMapper;
import com.example.backend_pj4.application.port.in.genre.GetGenreByIdUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.repository.GenreRepository;

@Service
public class GetGenreByIdService implements GetGenreByIdUseCase {

    private final GenreRepository genreRepository;

    public GetGenreByIdService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public GenreResult execute(String identifier) {
        return genreRepository.findBySlug(identifier)
                .or(() -> genreRepository.findById(identifier))
                .map(GenreResultMapper::toResult)
                .orElseThrow(() -> new CustomException(ErrorCode.GENRE_NOT_FOUND));
    }
}
