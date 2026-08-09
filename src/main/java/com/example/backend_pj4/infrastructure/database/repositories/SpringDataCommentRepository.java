package com.example.backend_pj4.infrastructure.database.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend_pj4.infrastructure.database.entities.CommentJpaEntity;

public interface SpringDataCommentRepository extends JpaRepository<CommentJpaEntity, String> {
    List<CommentJpaEntity> findByMovie_Id(String movieId);
    List<CommentJpaEntity> findByMovie_IdAndParentIsNull(String movieId);
    List<CommentJpaEntity> findByEpisode_Id(String episodeId);
    List<CommentJpaEntity> findByUser_Id(String userId);
    @Query("SELECT c FROM CommentJpaEntity c WHERE c.movie.id = :movieId AND c.isApproved = true")
    List<CommentJpaEntity> findApprovedByMovieId(@Param("movieId") String movieId);
    long countByMovie_Id(String movieId);
}


