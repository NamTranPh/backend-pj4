package com.example.backend_pj4.infrastructure.databases.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.entities.ListFavorite;
import com.example.backend_pj4.infrastructure.databases.entities.ListFavoriteEntity;

@Component
public class FavoriteMapper {

    private final UserMapper userMapper;
    private final MovieMapper movieMapper;

    public FavoriteMapper(UserMapper userMapper, @Lazy MovieMapper movieMapper) {
        this.userMapper = userMapper;
        this.movieMapper = movieMapper;
    }

    public ListFavorite toDomain(ListFavoriteEntity entity) {
        if (entity == null) return null;

        return ListFavorite.builder()
                .favoriteId(entity.getFavoriteId())
                .user(userMapper.toDomain(entity.getUser()))
                .movie(movieMapper.toSimpleDomain(entity.getMovie()))
                .addedAt(entity.getAddedAt())
                .build();
    }

    public ListFavoriteEntity toEntity(ListFavorite domain) {
        if (domain == null) return null;

        ListFavoriteEntity entity = new ListFavoriteEntity();
        entity.setFavoriteId(domain.getFavoriteId());
        entity.setUser(userMapper.toEntity(domain.getUser()));
        entity.setMovie(movieMapper.toEntity(domain.getMovie()));
        entity.setAddedAt(domain.getAddedAt());
        return entity;
    }

    public List<ListFavorite> toDomainList(List<ListFavoriteEntity> entities) {
        if (entities == null || entities.isEmpty()) return List.of();
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    public List<ListFavoriteEntity> toEntityList(List<ListFavorite> domains) {
        if (domains == null || domains.isEmpty()) return List.of();
        return domains.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public ListFavorite toSimpleDomain(ListFavoriteEntity entity) {
        if (entity == null) return null;
        return ListFavorite.builder()
                .favoriteId(entity.getFavoriteId())
                .addedAt(entity.getAddedAt())
                .build();
    }
}
