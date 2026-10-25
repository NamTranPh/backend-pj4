package com.example.backend_pj4.application.mapper;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.application.dto.actor.ActorResult;
import com.example.backend_pj4.application.dto.actor.MovieActorResult;
import com.example.backend_pj4.application.service.StorageUrlResolver;
import com.example.backend_pj4.domain.model.Actor;
import com.example.backend_pj4.domain.model.MovieActor;

@Component
public class ActorResultMapper {

    private final StorageUrlResolver storageUrlResolver;

    public ActorResultMapper(StorageUrlResolver storageUrlResolver) {
        this.storageUrlResolver = storageUrlResolver;
    }

    public ActorResult toResult(Actor actor) {
        if (actor == null) {
            return null;
        }
        return new ActorResult(
                actor.getId(),
                actor.getName(),
                actor.getOriginalName(),
                storageUrlResolver.resolveActorAvatar(actor.getAvatarUrl()),
                actor.getBio(),
                actor.getBirthDate(),
                actor.getNationality(),
                actor.getCreatedAt(),
                actor.getUpdatedAt()
        );
    }

    public MovieActorResult toMovieActorResult(MovieActor movieActor) {
        if (movieActor == null) {
            return null;
        }
        Actor actor = movieActor.getActor();
        return new MovieActorResult(
                movieActor.getActorId(),
                actor != null ? actor.getName() : null,
                actor != null ? actor.getOriginalName() : null,
                actor != null ? storageUrlResolver.resolveActorAvatar(actor.getAvatarUrl()) : null,
                actor != null ? actor.getNationality() : null,
                movieActor.getCharacterName(),
                movieActor.getOrderIndex()
        );
    }
}
