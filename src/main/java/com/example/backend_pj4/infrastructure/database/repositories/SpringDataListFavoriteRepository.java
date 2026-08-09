package com.example.backend_pj4.infrastructure.database.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend_pj4.infrastructure.database.entities.ListFavoriteJpaEntity;

public interface SpringDataListFavoriteRepository extends JpaRepository<ListFavoriteJpaEntity, String> {
    List<ListFavoriteJpaEntity> findByUser_Id(String userId);
    boolean existsByUser_IdAndMovie_Id(String userId, String movieId);
    @Modifying
    @Query("DELETE FROM ListFavoriteJpaEntity f WHERE f.user.id = :userId AND f.movie.id = :movieId")
    void deleteByUserIdAndMovieId(@Param("userId") String userId, @Param("movieId") String movieId);
    long countByUser_Id(String userId);
}


