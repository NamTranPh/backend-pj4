package com.example.backend_pj4.infrastructure.databases.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.domain.entities.Comment;
import com.example.backend_pj4.domain.repository.CommentRepository;
import com.example.backend_pj4.infrastructure.databases.mapper.CommentMapper;
import com.example.backend_pj4.infrastructure.databases.repository.JpaCommentRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {

    private final JpaCommentRepository jpaCommentRepository;
    private final CommentMapper commentMapper;

    @Override
    public Comment save(Comment comment) {
        var entity = commentMapper.toEntity(comment);
        var saved = jpaCommentRepository.save(entity);
        return commentMapper.toDomain(saved);
    }

    @Override
    public Optional<Comment> findById(String commentId) {
        return jpaCommentRepository.findById(commentId)
                .map(commentMapper::toDomain);
    }

    @Override
    public List<Comment> findAll() {
        return jpaCommentRepository.findAll().stream()
                .map(commentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String commentId) {
        jpaCommentRepository.deleteById(commentId);
    }

    @Override
    public List<Comment> findByMovieId(String movieId) {
        return jpaCommentRepository.findByMovieMovieId(movieId).stream()
                .map(commentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Comment> findByMovieIdAndParentIsNull(String movieId) {
        return jpaCommentRepository.findByMovieMovieIdAndParentIsNull(movieId).stream()
                .map(commentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Comment> findByParentId(String parentId) {
        return jpaCommentRepository.findByParentCommentId(parentId).stream()
                .map(commentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Comment> findByEpisodeId(String episodeId) {
        return jpaCommentRepository.findByEpisodeEpisodeId(episodeId).stream()
                .map(commentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Comment> findByEpisodeIdAndParentIsNull(String episodeId) {
        return jpaCommentRepository.findByEpisodeEpisodeIdAndParentIsNull(episodeId).stream()
                .map(commentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Comment> findByUserId(String userId) {
        return jpaCommentRepository.findByUserUserId(userId).stream()
                .map(commentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Comment> findApprovedCommentsByMovieId(String movieId) {
        return jpaCommentRepository.findByMovieMovieIdAndIsApprovedTrue(movieId).stream()
                .map(commentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Comment> findApprovedCommentsByEpisodeId(String episodeId) {
        return jpaCommentRepository.findByEpisodeEpisodeIdAndIsApprovedTrue(episodeId).stream()
                .map(commentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Comment> findPendingComments() {
        return jpaCommentRepository.findPendingComments().stream()
                .map(commentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Comment> findByMovieIdOrderByLikeCountDesc(String movieId) {
        return jpaCommentRepository.findByMovieMovieIdOrderByLikeCountDesc(movieId).stream()
                .map(commentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Comment> findTopCommentsByMovieId(String movieId, int limit) {
        return jpaCommentRepository.findTopCommentsByMovieId(movieId, limit).stream()
                .map(commentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByMovieId(String movieId) {
        return jpaCommentRepository.countByMovieMovieId(movieId);
    }

    @Override
    public long countByEpisodeId(String episodeId) {
        return jpaCommentRepository.countByEpisodeEpisodeId(episodeId);
    }

    @Override
    public long countByUserId(String userId) {
        return jpaCommentRepository.countByUserUserId(userId);
    }

    @Override
    public long countPendingComments() {
        return jpaCommentRepository.countByIsApprovedFalse();
    }
}
