package com.example.backend_pj4.infrastructure.databases.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.backend_pj4.infrastructure.databases.entities.CommentEntity;

@Repository
public interface JpaCommentRepository extends JpaRepository<CommentEntity, String> {
    
    List<CommentEntity> findByMovieMovieId(String movieId);
    
    List<CommentEntity> findByMovieMovieIdAndParentIsNull(String movieId);
    
    List<CommentEntity> findByParentCommentId(String parentId);
    
    List<CommentEntity> findByEpisodeEpisodeId(String episodeId);
    
    List<CommentEntity> findByEpisodeEpisodeIdAndParentIsNull(String episodeId);
    
    List<CommentEntity> findByUserUserId(String userId);
    
    List<CommentEntity> findByMovieMovieIdAndIsApprovedTrue(String movieId);
    
    List<CommentEntity> findByEpisodeEpisodeIdAndIsApprovedTrue(String episodeId);
    
    @Query("SELECT c FROM CommentEntity c WHERE c.isApproved = false ORDER BY c.createdAt DESC")
    List<CommentEntity> findPendingComments();
    
    List<CommentEntity> findByMovieMovieIdOrderByLikeCountDesc(String movieId);
    
    @Query("SELECT c FROM CommentEntity c WHERE c.movie.movieId = ?1 ORDER BY c.likeCount DESC LIMIT ?2")
    List<CommentEntity> findTopCommentsByMovieId(String movieId, int limit);
    
    long countByMovieMovieId(String movieId);
    
    long countByEpisodeEpisodeId(String episodeId);
    
    long countByUserUserId(String userId);
    
    long countByIsApprovedFalse();
}
