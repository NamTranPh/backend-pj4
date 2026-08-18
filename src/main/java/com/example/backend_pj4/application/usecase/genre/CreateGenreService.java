package com.example.backend_pj4.application.usecase.genre;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.genre.CreateGenreCommand;
import com.example.backend_pj4.application.dto.genre.GenreResult;
import com.example.backend_pj4.application.mapper.GenreResultMapper;
import com.example.backend_pj4.application.port.in.genre.CreateGenreUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.GenreStatus;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.common.util.SlugUtils;
import com.example.backend_pj4.domain.model.Genre;
import com.example.backend_pj4.domain.repository.GenreRepository;

@Service
public class CreateGenreService implements CreateGenreUseCase {

    private final GenreRepository genreRepository;

    public CreateGenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    @Transactional
    public GenreResult execute(CreateGenreCommand command) {
        // Kiểm tra tên trùng
        if (genreRepository.existsByName(command.name())) {
            throw new CustomException(ErrorCode.GENRE_NAME_ALREADY_EXISTS);
        }

        // Tạo slug từ tên
        String slug = SlugUtils.generateSlug(command.name());
        if (genreRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        Genre genre = Genre.builder()
                .name(command.name())
                .slug(slug)
                .icon(command.icon())
                .description(command.description())
                .status(GenreStatus.ACTIVE)
                .build();

        Genre saved = genreRepository.save(genre);
        return GenreResultMapper.toResult(saved);
    }
}
