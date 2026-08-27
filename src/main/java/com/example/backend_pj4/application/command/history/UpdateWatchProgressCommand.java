package com.example.backend_pj4.application.command.history;

public record UpdateWatchProgressCommand(
        String userId,
        String movieId,
        String episodeId,
        Integer positionSeconds,
        Integer durationSeconds
) {
}
