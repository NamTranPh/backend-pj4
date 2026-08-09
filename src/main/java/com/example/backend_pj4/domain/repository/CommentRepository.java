package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.domain.model.Comment;

public interface CommentRepository {
    Comment save(Comment comment);
    Optional<Comment> findById(String id);
    List<Comment> findAll();
    void deleteById(String id);
    List<Comment> findByMovieId(String movieId);
    List<Comment> findByMovieIdAndParentIsNull(String movieId);
    List<Comment> findByEpisodeId(String episodeId);
    List<Comment> findByUserId(String userId);
    List<Comment> findApprovedByMovieId(String movieId);
    long countByMovieId(String movieId);
}
