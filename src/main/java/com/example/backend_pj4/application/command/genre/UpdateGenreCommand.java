package com.example.backend_pj4.application.command.genre;

import com.example.backend_pj4.common.constants.enums.GenreStatus;

public record UpdateGenreCommand(String id, String name, String icon, String description, GenreStatus status) {
}
