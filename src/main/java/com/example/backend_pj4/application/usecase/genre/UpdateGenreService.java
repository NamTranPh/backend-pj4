package com.example.backend_pj4.application.usecase.genre;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.genre.UpdateGenreCommand;
import com.example.backend_pj4.application.dto.genre.GenreResult;
import com.example.backend_pj4.application.mapper.GenreResultMapper;
import com.example.backend_pj4.application.port.in.genre.UpdateGenreUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.common.util.SlugUtils;
import com.example.backend_pj4.domain.model.Genre;
import com.example.backend_pj4.domain.repository.GenreRepository;

@Service
public class UpdateGenreService implements UpdateGenreUseCase {

    private final GenreRepository genreRepository;

    public UpdateGenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    @Transactional
    public GenreResult execute(UpdateGenreCommand command) {
        Genre genre = genreRepository.findById(command.id())
                .orElseThrow(() -> new CustomException(ErrorCode.GENRE_NOT_FOUND));

        Genre.GenreBuilder builder = genre.toBuilder();

        // Cập nhật tên nếu thay đổi
        if (command.name() != null && !command.name().equals(genre.getName())) {
            if (genreRepository.existsByName(command.name())) {
                throw new CustomException(ErrorCode.GENRE_NAME_ALREADY_EXISTS);
            }
            builder.name(command.name());
            builder.slug(SlugUtils.generateSlug(command.name()));
        }

        if (command.icon() != null) {
            builder.icon(command.icon());
        }
        if (command.description() != null) {
            builder.description(command.description());
        }
        if (command.status() != null) {
            builder.status(command.status());
        }

        Genre saved = genreRepository.save(builder.build());
        return GenreResultMapper.toResult(saved);
    }
}
