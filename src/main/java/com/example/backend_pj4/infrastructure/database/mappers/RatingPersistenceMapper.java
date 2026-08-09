package com.example.backend_pj4.infrastructure.database.mappers;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.model.Rating;
import com.example.backend_pj4.infrastructure.database.entities.MovieJpaEntity;
import com.example.backend_pj4.infrastructure.database.entities.RatingJpaEntity;
import com.example.backend_pj4.infrastructure.database.entities.UserJpaEntity;

@Component
public class RatingPersistenceMapper {

    public Rating toDomain(RatingJpaEntity entity) {
        if (entity == null) return null;
        return Rating.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .movieId(entity.getMovie() != null ? entity.getMovie().getId() : null)
                .score(entity.getScore())
                .review(entity.getReview())
                .deletedAt(entity.getDeletedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public RatingJpaEntity toEntity(Rating domain) {
        if (domain == null) return null;
        RatingJpaEntity entity = new RatingJpaEntity();
        entity.setId(domain.getId());
        entity.setScore(domain.getScore());
        entity.setReview(domain.getReview());
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


