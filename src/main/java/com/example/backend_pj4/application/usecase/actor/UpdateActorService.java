package com.example.backend_pj4.application.usecase.actor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.actor.UpdateActorCommand;
import com.example.backend_pj4.application.dto.actor.ActorResult;
import com.example.backend_pj4.application.mapper.ActorResultMapper;
import com.example.backend_pj4.application.port.in.actor.UpdateActorUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Actor;
import com.example.backend_pj4.domain.repository.ActorRepository;

@Service
public class UpdateActorService implements UpdateActorUseCase {

    private final ActorRepository actorRepository;
    private final ActorResultMapper actorResultMapper;

    public UpdateActorService(ActorRepository actorRepository, ActorResultMapper actorResultMapper) {
        this.actorRepository = actorRepository;
        this.actorResultMapper = actorResultMapper;
    }

    @Override
    @Transactional
    public ActorResult execute(String id, UpdateActorCommand command) {
        Actor actor = actorRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ACTOR_NOT_FOUND));

        Actor updated = actor.toBuilder()
                .name(command.name())
                .originalName(command.originalName())
                .avatarUrl(command.avatarUrl() != null ? command.avatarUrl() : actor.getAvatarUrl())
                .bio(command.bio())
                .birthDate(command.birthDate())
                .nationality(command.nationality())
                .build();

        Actor saved = actorRepository.save(updated);
        return actorResultMapper.toResult(saved);
    }
}
