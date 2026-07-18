package com.example.backend_pj4.infrastructure.databases.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.backend_pj4.infrastructure.databases.entities.ListFavoriteEntity;

@Repository
public interface JpaFavoriteRepository extends JpaRepository<ListFavoriteEntity, String> {
    
    List<ListFavoriteEntity> findByUserUserId(String userId);
    
    Optional<ListFavoriteEntity> findByUserUserIdAndMovieMovieId(String userId, String movieId);
    
    boolean existsByUserUserIdAndMovieMovieId(String userId, String movieId);
    
    void deleteByUserUserIdAndMovieMovieId(String userId, String movieId);
    
    long countByUserUserId(String userId);
    
    long countByMovieMovieId(String movieId);
    
    @Query("SELECT f FROM ListFavoriteEntity f WHERE f.user.userId = ?1 ORDER BY f.addedAt DESC")
    Page<ListFavoriteEntity> findByUserUserIdOrderByAddedAtDesc(String userId, Pageable pageable);
}
