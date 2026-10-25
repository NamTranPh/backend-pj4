package com.example.backend_pj4.application.command.actor;

import java.time.LocalDate;

public record CreateActorCommand(
        String name,
        String originalName,
        String avatarUrl,
        String bio,
        LocalDate birthDate,
        String nationality
) {
}
