package com.example.backend_pj4.application.usecase.actor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.actor.ActorResult;
import com.example.backend_pj4.application.mapper.ActorResultMapper;
import com.example.backend_pj4.application.port.in.actor.GetActorByIdUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Actor;
import com.example.backend_pj4.domain.repository.ActorRepository;

@Service
public class GetActorByIdService implements GetActorByIdUseCase {

    private final ActorRepository actorRepository;
    private final ActorResultMapper actorResultMapper;

    public GetActorByIdService(ActorRepository actorRepository, ActorResultMapper actorResultMapper) {
        this.actorRepository = actorRepository;
        this.actorResultMapper = actorResultMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public ActorResult execute(String id) {
        Actor actor = actorRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ACTOR_NOT_FOUND));
        return actorResultMapper.toResult(actor);
    }
}
