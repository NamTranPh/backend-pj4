package com.example.backend_pj4.application.command.actor;

public record MovieActorAssignmentCommand(
        String actorId,
        String characterName,
        Integer orderIndex
) {
}
