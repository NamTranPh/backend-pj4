package com.example.backend_pj4.application.port.in.actor;

import com.example.backend_pj4.application.command.actor.UpdateActorCommand;
import com.example.backend_pj4.application.dto.actor.ActorResult;

public interface UpdateActorUseCase {
    ActorResult execute(String id, UpdateActorCommand command);
}
