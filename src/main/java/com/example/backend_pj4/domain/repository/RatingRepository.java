package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.domain.entities.Rating;

public interface RatingRepository {
    Rating save(Rating rating);
    Optional<Rating> findById(String ratingId);
    List<Rating> findAll();
    void deleteById(String ratingId);

    // User-Movie rating
    Optional<Rating> findByUserIdAndMovieId(String userId, String movieId);
    boolean existsByUserIdAndMovieId(String userId, String movieId);

    // Movie ratings
    List<Rating> findByMovieId(String movieId);
    List<Rating> findByMovieIdOrderByScoreDesc(String movieId);
    List<Rating> findByMovieIdAndScoreGreaterThan(String movieId, Integer score);

    // User ratings
    List<Rating> findByUserId(String userId);
    List<Rating> findByUserIdOrderByCreatedAtDesc(String userId);

    // Statistics
    Double calculateAverageRatingByMovieId(String movieId);
    long countByMovieId(String movieId);
    long countByScore(Integer score);

    // Top rated movies
    List<Object[]> findTopRatedMovies(int limit);  // Returns movieId, avgRating, ratingCount
}
