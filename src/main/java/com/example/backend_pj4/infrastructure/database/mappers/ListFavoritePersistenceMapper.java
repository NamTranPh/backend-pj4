package com.example.backend_pj4.infrastructure.database.mappers;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.model.ListFavorite;
import com.example.backend_pj4.infrastructure.database.entities.ListFavoriteJpaEntity;
import com.example.backend_pj4.infrastructure.database.entities.MovieJpaEntity;
import com.example.backend_pj4.infrastructure.database.entities.UserJpaEntity;

@Component
public class ListFavoritePersistenceMapper {

    public ListFavorite toDomain(ListFavoriteJpaEntity entity) {
        if (entity == null) return null;
        return ListFavorite.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .movieId(entity.getMovie() != null ? entity.getMovie().getId() : null)
                .deletedAt(entity.getDeletedAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public ListFavoriteJpaEntity toEntity(ListFavorite domain) {
        if (domain == null) return null;
        ListFavoriteJpaEntity entity = new ListFavoriteJpaEntity();
        entity.setId(domain.getId());
        entity.setDeletedAt(domain.getDeletedAt());
        if (domain.getUserId() != null) {
            UserJpaEntity user = new UserJpaEntity();
            user.setId(domain.getUserId());
            entity.setUser(user);
        }
        if (domain.getMovieId() != null) {
            MovieJpaEntity movie = new MovieJpaEntity();
            movie.setId(domain.getMovieId());
            entity.setMovie(movie);
        }
        return entity;
    }
}


