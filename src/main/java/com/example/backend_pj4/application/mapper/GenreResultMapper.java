package com.example.backend_pj4.application.mapper;

import com.example.backend_pj4.application.dto.genre.GenreResult;
import com.example.backend_pj4.domain.model.Genre;

public class GenreResultMapper {

    public static GenreResult toResult(Genre genre) {
        return new GenreResult(
                genre.getId(),
                genre.getName(),
                genre.getSlug(),
                genre.getIcon(),
                genre.getDescription(),
                genre.getStatus(),
                genre.getCreatedAt(),
                genre.getUpdatedAt()
        );
    }
}
