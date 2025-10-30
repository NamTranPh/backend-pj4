package com.example.backend_pj4.infrastructure.databases.mapper;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.entities.Rating;
import com.example.backend_pj4.infrastructure.databases.entities.RatingEntity;

@Component
public class RatingMapper {

    private final UserMapper userMapper;
    private final MovieMapper movieMapper;

    public RatingMapper(
            UserMapper userMapper,
            @Lazy MovieMapper movieMapper // tránh loop giữa MovieMapper ↔ RatingMapper
    ) {
        this.userMapper = userMapper;
        this.movieMapper = movieMapper;
    }

    // ==============================
    // Entity → Domain
    // ==============================
    public Rating toDomain(RatingEntity entity) {
        if (entity == null) return null;

        return Rating.builder()
                .ratingId(entity.getRatingId())
                .user(userMapper.toDomain(entity.getUser()))
                .movie(movieMapper.toSimpleDomain(entity.getMovie())) // tránh mapping sâu để không loop
                .score(entity.getScore())
                .review(entity.getReview())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // ==============================
    // Domain → Entity
    // ==============================
    public RatingEntity toEntity(Rating domain) {
        if (domain == null) return null;

        RatingEntity entity = new RatingEntity();
        entity.setRatingId(domain.getRatingId());
        entity.setUser(userMapper.toEntity(domain.getUser()));
        entity.setMovie(movieMapper.toEntity(domain.getMovie()));
        entity.setScore(domain.getScore());
        entity.setReview(domain.getReview());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}
