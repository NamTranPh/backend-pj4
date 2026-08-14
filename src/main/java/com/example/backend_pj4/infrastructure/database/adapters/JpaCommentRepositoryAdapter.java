package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.domain.model.Comment;
import com.example.backend_pj4.domain.repository.CommentRepository;
import com.example.backend_pj4.infrastructure.database.mappers.CommentPersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.CommentJpaRepository;

@Repository
public class JpaCommentRepositoryAdapter implements CommentRepository {

    private final CommentJpaRepository commentJpaRepository;
    private final CommentPersistenceMapper commentPersistenceMapper;

    public JpaCommentRepositoryAdapter(CommentJpaRepository commentJpaRepository, CommentPersistenceMapper commentPersistenceMapper) {
        this.commentJpaRepository = commentJpaRepository;
        this.commentPersistenceMapper = commentPersistenceMapper;
    }

    @Override
    public Comment save(Comment comment) {
        return commentPersistenceMapper.toDomain(commentJpaRepository.save(commentPersistenceMapper.toEntity(comment)));
    }

    @Override
    public Optional<Comment> findById(String id) {
        return commentJpaRepository.findById(id).map(commentPersistenceMapper::toDomain);
    }

    @Override
    public List<Comment> findAll() {
        return commentJpaRepository.findAll().stream().map(commentPersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        commentJpaRepository.deleteById(id);
    }

    @Override
    public List<Comment> findByMovieId(String movieId) {
        return commentJpaRepository.findByMovie_Id(movieId).stream().map(commentPersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Comment> findByMovieIdAndParentIsNull(String movieId) {
        return commentJpaRepository.findByMovie_IdAndParentIsNull(movieId).stream().map(commentPersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Comment> findByEpisodeId(String episodeId) {
        return commentJpaRepository.findByEpisode_Id(episodeId).stream().map(commentPersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Comment> findByUserId(String userId) {
        return commentJpaRepository.findByUser_Id(userId).stream().map(commentPersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Comment> findApprovedByMovieId(String movieId) {
        return commentJpaRepository.findApprovedByMovieId(movieId).stream().map(commentPersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public long countByMovieId(String movieId) {
        return commentJpaRepository.countByMovie_Id(movieId);
    }
}


