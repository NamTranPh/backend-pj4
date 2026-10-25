package com.example.backend_pj4.infrastructure.database.mappers;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.model.Actor;
import com.example.backend_pj4.domain.model.MovieActor;
import com.example.backend_pj4.infrastructure.database.entities.ActorJpaEntity;
import com.example.backend_pj4.infrastructure.database.entities.MovieActorJpaEntity;

@Component
public class ActorPersistenceMapper {

    public Actor toDomain(ActorJpaEntity entity) {
        if (entity == null) return null;
        return Actor.builder()
                .id(entity.getId())
                .name(entity.getName())
                .originalName(entity.getOriginalName())
                .avatarUrl(entity.getAvatarUrl())
                .bio(entity.getBio())
                .birthDate(entity.getBirthDate())
                .nationality(entity.getNationality())
                .deletedAt(entity.getDeletedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public ActorJpaEntity toEntity(Actor domain) {
        if (domain == null) return null;
        ActorJpaEntity entity = new ActorJpaEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setOriginalName(domain.getOriginalName());
        entity.setAvatarUrl(domain.getAvatarUrl());
        entity.setBio(domain.getBio());
        entity.setBirthDate(domain.getBirthDate());
        entity.setNationality(domain.getNationality());
        entity.setDeletedAt(domain.getDeletedAt());
        return entity;
    }

    public MovieActor toDomain(MovieActorJpaEntity entity) {
        if (entity == null) return null;
        return MovieActor.builder()
                .id(entity.getId())
                .movieId(entity.getMovie() != null ? entity.getMovie().getId() : null)
                .actorId(entity.getActor() != null ? entity.getActor().getId() : null)
                .actor(toDomain(entity.getActor()))
                .characterName(entity.getCharacterName())
                .orderIndex(entity.getOrderIndex())
                .build();
    }

    public MovieActorJpaEntity toEntity(MovieActor domain) {
        if (domain == null) return null;
        MovieActorJpaEntity entity = new MovieActorJpaEntity();
        entity.setId(domain.getId());
        if (domain.getActor() != null) {
            entity.setActor(toEntity(domain.getActor()));
        } else if (domain.getActorId() != null) {
            ActorJpaEntity actor = new ActorJpaEntity();
            actor.setId(domain.getActorId());
            entity.setActor(actor);
        }
        entity.setCharacterName(domain.getCharacterName());
        entity.setOrderIndex(domain.getOrderIndex() != null ? domain.getOrderIndex() : 0);
        return entity;
    }
}
