package com.example.backend_pj4.infrastructure.databases.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.example.backend_pj4.domain.entities.ListFavorite;
import com.example.backend_pj4.domain.repository.FavoriteRepository;
import com.example.backend_pj4.infrastructure.databases.mapper.FavoriteMapper;
import com.example.backend_pj4.infrastructure.databases.repository.JpaFavoriteRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FavoriteRepositoryImpl implements FavoriteRepository {

    private final JpaFavoriteRepository jpaFavoriteRepository;
    private final FavoriteMapper favoriteMapper;

    @Override
    public ListFavorite save(ListFavorite favorite) {
        var entity = favoriteMapper.toEntity(favorite);
        var saved = jpaFavoriteRepository.save(entity);
        return favoriteMapper.toDomain(saved);
    }

    @Override
    public Optional<ListFavorite> findById(String id) {
        return jpaFavoriteRepository.findById(id)
                .map(favoriteMapper::toDomain);
    }

    @Override
    public List<ListFavorite> findAll() {
        return jpaFavoriteRepository.findAll().stream()
                .map(favoriteMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        jpaFavoriteRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return jpaFavoriteRepository.existsById(id);
    }

    @Override
    public Optional<ListFavorite> findByUserAndMovie(String userId, String movieId) {
        return jpaFavoriteRepository.findByUserUserIdAndMovieMovieId(userId, movieId)
                .map(favoriteMapper::toDomain);
    }

    @Override
    public List<ListFavorite> findByUserId(String userId) {
        return jpaFavoriteRepository.findByUserUserId(userId).stream()
                .map(favoriteMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByUserAndMovie(String userId, String movieId) {
        return jpaFavoriteRepository.existsByUserUserIdAndMovieMovieId(userId, movieId);
    }

    @Override
    public void deleteByUserAndMovie(String userId, String movieId) {
        jpaFavoriteRepository.deleteByUserUserIdAndMovieMovieId(userId, movieId);
    }

    @Override
    public long countByUserId(String userId) {
        return jpaFavoriteRepository.countByUserUserId(userId);
    }
}
