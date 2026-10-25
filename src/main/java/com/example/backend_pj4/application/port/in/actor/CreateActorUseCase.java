package com.example.backend_pj4.application.port.in.actor;

import com.example.backend_pj4.application.command.actor.CreateActorCommand;
import com.example.backend_pj4.application.dto.actor.ActorResult;

public interface CreateActorUseCase {
    ActorResult execute(CreateActorCommand command);
}
