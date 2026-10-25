package com.example.backend_pj4.application.usecase.actor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.port.in.actor.DeleteActorUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.repository.ActorRepository;

@Service
public class DeleteActorService implements DeleteActorUseCase {

    private final ActorRepository actorRepository;

    public DeleteActorService(ActorRepository actorRepository) {
        this.actorRepository = actorRepository;
    }

    @Override
    @Transactional
    public void execute(String id) {
        if (actorRepository.findById(id).isEmpty()) {
            throw new CustomException(ErrorCode.ACTOR_NOT_FOUND);
        }
        actorRepository.deleteById(id);
    }
}
