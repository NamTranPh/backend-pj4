package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.domain.model.Comment;
import com.example.backend_pj4.domain.repository.CommentRepository;
import com.example.backend_pj4.infrastructure.database.mappers.CommentPersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.SpringDataCommentRepository;

@Repository
public class JpaCommentRepositoryAdapter implements CommentRepository {

    private final SpringDataCommentRepository SpringDataCommentRepository;
    private final CommentPersistenceMapper CommentPersistenceMapper;

    public JpaCommentRepositoryAdapter(SpringDataCommentRepository SpringDataCommentRepository, CommentPersistenceMapper CommentPersistenceMapper) {
        this.SpringDataCommentRepository = SpringDataCommentRepository;
        this.CommentPersistenceMapper = CommentPersistenceMapper;
    }

    @Override
    public Comment save(Comment comment) {
        return CommentPersistenceMapper.toDomain(SpringDataCommentRepository.save(CommentPersistenceMapper.toEntity(comment)));
    }

    @Override
    public Optional<Comment> findById(String id) {
        return SpringDataCommentRepository.findById(id).map(CommentPersistenceMapper::toDomain);
    }

    @Override
    public List<Comment> findAll() {
        return SpringDataCommentRepository.findAll().stream().map(CommentPersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        SpringDataCommentRepository.deleteById(id);
    }

    @Override
    public List<Comment> findByMovieId(String movieId) {
        return SpringDataCommentRepository.findByMovie_Id(movieId).stream().map(CommentPersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Comment> findByMovieIdAndParentIsNull(String movieId) {
        return SpringDataCommentRepository.findByMovie_IdAndParentIsNull(movieId).stream().map(CommentPersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Comment> findByEpisodeId(String episodeId) {
        return SpringDataCommentRepository.findByEpisode_Id(episodeId).stream().map(CommentPersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Comment> findByUserId(String userId) {
        return SpringDataCommentRepository.findByUser_Id(userId).stream().map(CommentPersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Comment> findApprovedByMovieId(String movieId) {
        return SpringDataCommentRepository.findApprovedByMovieId(movieId).stream().map(CommentPersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public long countByMovieId(String movieId) {
        return SpringDataCommentRepository.countByMovie_Id(movieId);
    }
}


