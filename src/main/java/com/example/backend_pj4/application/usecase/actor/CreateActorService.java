package com.example.backend_pj4.application.usecase.actor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.actor.CreateActorCommand;
import com.example.backend_pj4.application.dto.actor.ActorResult;
import com.example.backend_pj4.application.mapper.ActorResultMapper;
import com.example.backend_pj4.application.port.in.actor.CreateActorUseCase;
import com.example.backend_pj4.domain.model.Actor;
import com.example.backend_pj4.domain.repository.ActorRepository;

@Service
public class CreateActorService implements CreateActorUseCase {

    private final ActorRepository actorRepository;
    private final ActorResultMapper actorResultMapper;

    public CreateActorService(ActorRepository actorRepository, ActorResultMapper actorResultMapper) {
        this.actorRepository = actorRepository;
        this.actorResultMapper = actorResultMapper;
    }

    @Override
    @Transactional
    public ActorResult execute(CreateActorCommand command) {
        Actor actor = Actor.builder()
                .name(command.name())
                .originalName(command.originalName())
                .avatarUrl(command.avatarUrl())
                .bio(command.bio())
                .birthDate(command.birthDate())
                .nationality(command.nationality())
                .build();

        Actor saved = actorRepository.save(actor);
        return actorResultMapper.toResult(saved);
    }
}
