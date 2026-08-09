package com.example.backend_pj4.infrastructure.database.mappers;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.model.Comment;
import com.example.backend_pj4.infrastructure.database.entities.CommentJpaEntity;
import com.example.backend_pj4.infrastructure.database.entities.EpisodeJpaEntity;
import com.example.backend_pj4.infrastructure.database.entities.MovieJpaEntity;
import com.example.backend_pj4.infrastructure.database.entities.UserJpaEntity;

@Component
public class CommentPersistenceMapper {

    public Comment toDomain(CommentJpaEntity entity) {
        if (entity == null) return null;
        return Comment.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .movieId(entity.getMovie() != null ? entity.getMovie().getId() : null)
                .episodeId(entity.getEpisode() != null ? entity.getEpisode().getId() : null)
                .content(entity.getContent())
                .parentId(entity.getParent() != null ? entity.getParent().getId() : null)
                .isApproved(entity.getIsApproved())
                .likeCount(entity.getLikeCount())
                .deletedAt(entity.getDeletedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public CommentJpaEntity toEntity(Comment domain) {
        if (domain == null) return null;
        CommentJpaEntity entity = new CommentJpaEntity();
        entity.setId(domain.getId());
        entity.setContent(domain.getContent());
        entity.setIsApproved(domain.getIsApproved());
        entity.setLikeCount(domain.getLikeCount());
        entity.setDeletedAt(domain.getDeletedAt());
        if (domain.getUserId() != null) {
            UserJpaEntity user = new UserJpaEntity();
            user.setId(domain.getUserId());
            entity.setUser(user);
        }
        if (domain.getMovieId() != null) {
            MovieJpaEntity movie = new MovieJpaEntity();
            movie.setId(domain.getMovieId());
            entity.setMovie(movie);
        }
        if (domain.getEpisodeId() != null) {
            EpisodeJpaEntity episode = new EpisodeJpaEntity();
            episode.setId(domain.getEpisodeId());
            entity.setEpisode(episode);
        }
        if (domain.getParentId() != null) {
            CommentJpaEntity parent = new CommentJpaEntity();
            parent.setId(domain.getParentId());
            entity.setParent(parent);
        }
        return entity;
    }
}


