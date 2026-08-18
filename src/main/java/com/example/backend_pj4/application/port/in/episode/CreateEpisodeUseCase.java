package com.example.backend_pj4.application.port.in.episode;

import com.example.backend_pj4.application.command.episode.CreateEpisodeCommand;
import com.example.backend_pj4.application.dto.episode.EpisodeResult;

public interface CreateEpisodeUseCase {
    EpisodeResult execute(CreateEpisodeCommand command);
}
