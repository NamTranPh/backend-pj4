package com.example.backend_pj4.application.dto.actor;

public record MovieActorResult(
        String actorId,
        String name,
        String originalName,
        String avatarUrl,
        String nationality,
        String characterName,
        Integer orderIndex
) {
}
