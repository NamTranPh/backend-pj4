package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.domain.model.ListFavorite;

public interface FavoriteRepository {
    ListFavorite save(ListFavorite favorite);
    Optional<ListFavorite> findById(String id);
    void deleteById(String id);
    List<ListFavorite> findByUserId(String userId);
    boolean existsByUserIdAndMovieId(String userId, String movieId);
    void deleteByUserIdAndMovieId(String userId, String movieId);
    long countByUserId(String userId);
}
