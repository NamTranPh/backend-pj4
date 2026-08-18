package com.example.backend_pj4.application.usecase.genre;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.port.in.genre.DeleteGenreUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Genre;
import com.example.backend_pj4.domain.repository.GenreRepository;

@Service
public class DeleteGenreService implements DeleteGenreUseCase {

    private final GenreRepository genreRepository;

    public DeleteGenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    @Transactional
    public void execute(String id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.GENRE_NOT_FOUND));

        // Gỡ liên kết genre khỏi tất cả movie (xóa rows trong movie_genre)
        genreRepository.removeGenreFromAllMovies(id);

        // Xóa mềm genre
        Genre deleted = genre.toBuilder()
                .deletedAt(LocalDateTime.now())
                .build();
        genreRepository.save(deleted);
    }
}
