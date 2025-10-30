package com.example.backend_pj4.infrastructure.databases.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.entities.Genre;
import com.example.backend_pj4.infrastructure.databases.entities.GenreEntity;

@Component
public class GenreMapper {
    // ==============================
    // Entity → Domain
    // ==============================
    public Genre toDomain(GenreEntity entity) {
        if (entity == null)
            return null;

        return Genre.builder()
                .genreId(entity.getGenreId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // ==============================
    // Domain → Entity
    // ==============================
    public GenreEntity toEntity(Genre domain) {
        if (domain == null)
            return null;

        GenreEntity entity = new GenreEntity();
        entity.setGenreId(domain.getGenreId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    // ==============================
    // List mapping (optional)
    // ==============================
    public List<Genre> toDomainList(List<GenreEntity> entities) {
        if (entities == null || entities.isEmpty())
            return List.of();
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    public List<GenreEntity> toEntityList(List<Genre> domains) {
        if (domains == null || domains.isEmpty())
            return List.of();
        return domains.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
