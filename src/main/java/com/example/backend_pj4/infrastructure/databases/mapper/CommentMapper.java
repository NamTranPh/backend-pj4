package com.example.backend_pj4.infrastructure.databases.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.entities.Comment;
import com.example.backend_pj4.infrastructure.databases.entities.CommentEntity;

@Component
public class CommentMapper {

    private final UserMapper userMapper;
    private final MovieMapper movieMapper;
    private final EpisodeMapper episodeMapper;

    public CommentMapper(UserMapper userMapper, MovieMapper movieMapper, @Lazy EpisodeMapper episodeMapper) {
        this.userMapper = userMapper;
        this.movieMapper = movieMapper;
        this.episodeMapper = episodeMapper;
    }

    // ==============================
    // Entity → Domain
    // ==============================
    public Comment toDomain(CommentEntity entity) {
        if (entity == null)
            return null;

        return Comment.builder()
                .commentId(entity.getCommentId())
                .user(userMapper.toDomain(entity.getUser()))
                .movie(movieMapper.toDomain(entity.getMovie()))
                .episode(entity.getEpisode() != null
                        ? episodeMapper.toSimpleDomain(entity.getEpisode()) // tránh loop
                        : null)
                .content(entity.getContent())
                .isApproved(entity.getIsApproved())
                .likeCount(entity.getLikeCount())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                // tránh vòng lặp vô hạn giữa parent và replies
                .parent(entity.getParent() != null
                        ? Comment.builder().commentId(entity.getParent().getCommentId()).build()
                        : null)
                .replies(null) // replies sẽ được map riêng nếu cần
                .build();
    }

    // ==============================
    // Domain → Entity
    // ==============================
    public CommentEntity toEntity(Comment domain) {
        if (domain == null)
            return null;

        CommentEntity entity = new CommentEntity();
        entity.setCommentId(domain.getCommentId());
        entity.setUser(userMapper.toEntity(domain.getUser()));
        entity.setMovie(movieMapper.toEntity(domain.getMovie()));
        entity.setEpisode(domain.getEpisode() != null
                ? episodeMapper.toEntity(domain.getEpisode())
                : null);
        entity.setContent(domain.getContent());
        entity.setIsApproved(domain.getIsApproved());
        entity.setLikeCount(domain.getLikeCount());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setParent(domain.getParent() != null
                ? toEntity(Comment.builder().commentId(domain.getParent().getCommentId()).build())
                : null);
        // replies cũng để null, tránh loop
        return entity;
    }

    // ==============================
    // List mapping
    // ==============================
    public List<Comment> toDomainList(List<CommentEntity> entities) {
        if (entities == null)
            return List.of();
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    public List<CommentEntity> toEntityList(List<Comment> domains) {
        if (domains == null)
            return List.of();
        return domains.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    // ✅ Simple mapping – tránh vòng lặp
    public Comment toSimpleDomain(CommentEntity entity) {
        if (entity == null)
            return null;
        return Comment.builder()
                .commentId(entity.getCommentId())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
