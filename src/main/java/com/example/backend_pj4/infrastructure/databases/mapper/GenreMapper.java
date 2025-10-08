package com.example.backend_pj4.infrastructure.databases.mapper;

import com.example.backend_pj4.domain.entities.Genre;
import com.example.backend_pj4.infrastructure.databases.entities.GenreEntity;

public class GenreMapper {

    public static Genre toDomain(GenreEntity entity) {
        if (entity == null) return null;

        return Genre.builder()
                .genreId(entity.getGenreId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static GenreEntity toEntity(Genre domain) {
        if (domain == null) return null;

        GenreEntity entity = new GenreEntity();
        entity.setGenreId(domain.getGenreId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}
