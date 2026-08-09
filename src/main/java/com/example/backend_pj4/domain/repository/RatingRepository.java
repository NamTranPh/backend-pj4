package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.domain.model.Rating;

public interface RatingRepository {
    Rating save(Rating rating);
    Optional<Rating> findById(String id);
    List<Rating> findAll();
    void deleteById(String id);
    Optional<Rating> findByUserIdAndMovieId(String userId, String movieId);
    boolean existsByUserIdAndMovieId(String userId, String movieId);
    List<Rating> findByMovieId(String movieId);
    List<Rating> findByUserId(String userId);
    Double calculateAverageByMovieId(String movieId);
    long countByMovieId(String movieId);
}
