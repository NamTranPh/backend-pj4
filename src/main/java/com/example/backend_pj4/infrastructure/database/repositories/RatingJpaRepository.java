package com.example.backend_pj4.infrastructure.database.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend_pj4.infrastructure.database.entities.RatingJpaEntity;

public interface RatingJpaRepository extends JpaRepository<RatingJpaEntity, String> {
    Optional<RatingJpaEntity> findByUser_IdAndMovie_Id(String userId, String movieId);
    boolean existsByUser_IdAndMovie_Id(String userId, String movieId);
    List<RatingJpaEntity> findByMovie_Id(String movieId);
    List<RatingJpaEntity> findByUser_Id(String userId);
    @Query("SELECT AVG(r.score) FROM RatingJpaEntity r WHERE r.movie.id = :movieId")
    Double calculateAverageByMovieId(@Param("movieId") String movieId);
    long countByMovie_Id(String movieId);
}


