package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.domain.entities.ListFavorite;

public interface ListFavoriteRepository {
    // Basic CRUD
    ListFavorite save(ListFavorite favorite);
    Optional<ListFavorite> findById(String favoriteId);
    List<ListFavorite> findAll();
    void deleteById(String favoriteId);
    void deleteByUserIdAndMovieId(String userId, String movieId);
    
    // User favorites
    List<ListFavorite> findByUserId(String userId);
    List<ListFavorite> findByUserIdOrderByAddedAtDesc(String userId);
    
    // Movie favorites
    List<ListFavorite> findByMovieId(String movieId);
    
    // Check if exists
    boolean existsByUserIdAndMovieId(String userId, String movieId);
    Optional<ListFavorite> findByUserIdAndMovieId(String userId, String movieId);
    
    // Statistics
    long countByMovieId(String movieId);
    long countByUserId(String userId);
    
    // Popular movies by favorites
    List<Object[]> findMostFavoritedMovies(int limit); // Returns movieId, favoriteCount
}