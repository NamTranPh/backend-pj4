package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.domain.entities.Comment;

public interface CommentRepository {
    // Basic CRUD
    Comment save(Comment comment);

    Optional<Comment> findById(String commentId);

    List<Comment> findAll();

    void deleteById(String commentId);

    // Movie comments
    List<Comment> findByMovieId(String movieId);

    List<Comment> findByMovieIdAndParentIsNull(String movieId); // Top-level comments

    List<Comment> findByParentId(String parentId); // Replies

    // Episode comments
    List<Comment> findByEpisodeId(String episodeId);

    List<Comment> findByEpisodeIdAndParentIsNull(String episodeId);

    // User comments
    List<Comment> findByUserId(String userId);

    // Approved comments
    List<Comment> findApprovedCommentsByMovieId(String movieId);

    List<Comment> findApprovedCommentsByEpisodeId(String episodeId);

    List<Comment> findPendingComments();

    // Popular comments
    List<Comment> findByMovieIdOrderByLikeCountDesc(String movieId);

    List<Comment> findTopCommentsByMovieId(String movieId, int limit);

    // Statistics
    long countByMovieId(String movieId);

    long countByEpisodeId(String episodeId);

    long countByUserId(String userId);

    long countPendingComments();
}