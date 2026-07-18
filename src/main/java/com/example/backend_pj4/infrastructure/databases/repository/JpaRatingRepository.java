package com.example.backend_pj4.infrastructure.databases.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.backend_pj4.infrastructure.databases.entities.RatingEntity;

@Repository
public interface JpaRatingRepository extends JpaRepository<RatingEntity, String> {
    
    Optional<RatingEntity> findByUser_UserIdAndMovie_MovieId(String userId, String movieId);
    
    List<RatingEntity> findByMovie_MovieId(String movieId);
    
    Page<RatingEntity> findByMovie_MovieId(String movieId, Pageable pageable);
    
    long countByMovie_MovieId(String movieId);
    
    @Query("SELECT AVG(r.score) FROM RatingEntity r WHERE r.movie.movieId = :movieId")
    Double getAverageScoreByMovie(String movieId);
    
    @Query("SELECT AVG(r.score) FROM RatingEntity r WHERE r.movie.movieId = :movieId")
    Double findAverageRatingByMovieId(String movieId);
    
    @Query("SELECT COUNT(r) FROM RatingEntity r WHERE r.movie.movieId = :movieId")
    long countRatingsByMovieId(String movieId);
    
    boolean existsByUser_UserIdAndMovie_MovieId(String userId, String movieId);
    
    List<RatingEntity> findByUser_UserId(String userId);
    
    Page<RatingEntity> findByScoreBetween(Integer minScore, Integer maxScore, Pageable pageable);
}
