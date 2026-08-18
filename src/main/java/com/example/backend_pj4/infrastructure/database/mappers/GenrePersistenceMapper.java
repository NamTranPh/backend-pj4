package com.example.backend_pj4.infrastructure.database.mappers;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.model.Genre;
import com.example.backend_pj4.infrastructure.database.entities.GenreJpaEntity;

@Component
public class GenrePersistenceMapper {

    public Genre toDomain(GenreJpaEntity entity) {
        if (entity == null) return null;
        return Genre.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .icon(entity.getIcon())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .deletedAt(entity.getDeletedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public GenreJpaEntity toEntity(Genre domain) {
        if (domain == null) return null;
        GenreJpaEntity entity = new GenreJpaEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setSlug(domain.getSlug());
        entity.setIcon(domain.getIcon());
        entity.setDescription(domain.getDescription());
        entity.setStatus(domain.getStatus());
        entity.setDeletedAt(domain.getDeletedAt());
        return entity;
    }
}


