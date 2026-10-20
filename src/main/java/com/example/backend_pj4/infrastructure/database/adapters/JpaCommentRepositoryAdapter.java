package com.example.backend_pj4.infrastructure.database.adapters;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.Comment;
import com.example.backend_pj4.domain.repository.CommentRepository;
import com.example.backend_pj4.domain.repository.filter.CommentFilter;
import com.example.backend_pj4.infrastructure.database.entities.CommentJpaEntity;
import com.example.backend_pj4.infrastructure.database.entities.CommentLikeJpaEntity;
import com.example.backend_pj4.infrastructure.database.entities.UserJpaEntity;
import com.example.backend_pj4.infrastructure.database.mappers.CommentPersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.CommentJpaRepository;
import com.example.backend_pj4.infrastructure.database.repositories.CommentLikeJpaRepository;
import com.example.backend_pj4.infrastructure.database.specifications.CommentSpecification;

@Repository
public class JpaCommentRepositoryAdapter implements CommentRepository {

    private final CommentJpaRepository commentJpaRepository;
    private final CommentLikeJpaRepository commentLikeJpaRepository;
    private final CommentPersistenceMapper commentPersistenceMapper;

    public JpaCommentRepositoryAdapter(CommentJpaRepository commentJpaRepository,
                                       CommentLikeJpaRepository commentLikeJpaRepository,
                                       CommentPersistenceMapper commentPersistenceMapper) {
        this.commentJpaRepository = commentJpaRepository;
        this.commentLikeJpaRepository = commentLikeJpaRepository;
        this.commentPersistenceMapper = commentPersistenceMapper;
    }

    @Override
    public Comment save(Comment comment) {
        return commentPersistenceMapper.toDomain(
                commentJpaRepository.save(commentPersistenceMapper.toEntity(comment)));
    }

    @Override
    public Optional<Comment> findById(String id) {
        return commentJpaRepository.findById(id).map(commentPersistenceMapper::toDomain);
    }

    @Override
    public PageResult<Comment> find(CommentFilter filter, PageQuery query) {
        return PageableMapper.toPageResult(
                commentJpaRepository.findAll(
                        CommentSpecification.buildFilter(filter),
                        PageableMapper.toPageable(query)),
                commentPersistenceMapper::toDomain);
    }

    @Override
    public List<Comment> findByRootIds(Collection<String> rootIds) {
        if (rootIds == null || rootIds.isEmpty()) {
            return List.of();
        }
        return commentJpaRepository.findByRootIds(rootIds).stream()
                .map(commentPersistenceMapper::toDomain).toList();
    }

    @Override
    public void softDeleteByIds(Collection<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        commentJpaRepository.softDeleteByIds(ids, LocalDateTime.now());
    }

    @Override
    public void addLikeCount(String commentId, int delta) {
        commentJpaRepository.addLikeCount(commentId, delta);
    }

    @Override
    public void recountLikeCounts(Collection<String> commentIds) {
        if (commentIds == null || commentIds.isEmpty()) {
            return;
        }
        commentJpaRepository.recountLikeCounts(commentIds);
    }

    @Override
    public boolean existsLike(String userId, String commentId) {
        return commentLikeJpaRepository.existsByUser_IdAndComment_Id(userId, commentId);
    }

    @Override
    public void addLike(String userId, String commentId) {
        // Dựng thực thể rỗng chỉ có id thay vì nạp User/Comment thật: ghi một dòng khoá ngoại
        // không cần biết gì thêm về hai đầu quan hệ.
        UserJpaEntity user = new UserJpaEntity();
        user.setId(userId);
        CommentJpaEntity comment = new CommentJpaEntity();
        comment.setId(commentId);
        commentLikeJpaRepository.save(new CommentLikeJpaEntity(user, comment));
    }

    @Override
    public void removeLike(String userId, String commentId) {
        commentLikeJpaRepository.deleteByUser_IdAndComment_Id(userId, commentId);
    }

    @Override
    public List<String> findLikedIdsIn(String userId, Collection<String> commentIds) {
        if (userId == null || commentIds == null || commentIds.isEmpty()) {
            return List.of();
        }
        return commentLikeJpaRepository.findLikedCommentIds(userId, commentIds);
    }

    @Override
    public java.util.Map<String, Long> countByMovieIds(Collection<String> movieIds) {
        if (movieIds == null || movieIds.isEmpty()) {
            return java.util.Map.of();
        }
        List<Object[]> rows = commentJpaRepository.countGroupedByMovieIds(movieIds);
        java.util.Map<String, Long> result = new java.util.HashMap<>();
        for (Object[] row : rows) {
            String movieId = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            result.put(movieId, count);
        }
        return result;
    }
}
