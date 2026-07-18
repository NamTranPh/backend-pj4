package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.domain.entities.ListFavorite;

public interface FavoriteRepository {
    ListFavorite save(ListFavorite favorite);
    Optional<ListFavorite> findById(String id);
    List<ListFavorite> findAll();
    void deleteById(String id);
    boolean existsById(String id);
    Optional<ListFavorite> findByUserAndMovie(String userId, String movieId);
    List<ListFavorite> findByUserId(String userId);
    boolean existsByUserAndMovie(String userId, String movieId);
    void deleteByUserAndMovie(String userId, String movieId);
    long countByUserId(String userId);
}
