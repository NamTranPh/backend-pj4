package com.example.backend_pj4.application.port.in.episode;

public interface DeleteEpisodeUseCase {
    void execute(String movieId, String episodeId);
}
