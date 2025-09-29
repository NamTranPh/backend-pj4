package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.domain.entities.ListFavorite;

public interface ListFavoriteRepository {
    // Basic CRUD
    ListFavorite save(ListFavorite favorite);
    Optional<ListFavorite> findById(Integer favoriteId);
    List<ListFavorite> findAll();
    void deleteById(Integer favoriteId);
    void deleteByUserIdAndMovieId(Integer userId, Integer movieId);
    
    // User favorites
    List<ListFavorite> findByUserId(Integer userId);
    List<ListFavorite> findByUserIdOrderByAddedAtDesc(Integer userId);
    
    // Movie favorites
    List<ListFavorite> findByMovieId(Integer movieId);
    
    // Check if exists
    boolean existsByUserIdAndMovieId(Integer userId, Integer movieId);
    Optional<ListFavorite> findByUserIdAndMovieId(Integer userId, Integer movieId);
    
    // Statistics
    long countByMovieId(Integer movieId);
    long countByUserId(Integer userId);
    
    // Popular movies by favorites
    List<Object[]> findMostFavoritedMovies(int limit); // Returns movieId, favoriteCount
}