package com.example.backend_pj4.application.port.in.streaming;

import com.example.backend_pj4.application.dto.streaming.PlayContentResult;

public interface PlayEpisodeUseCase {
    PlayContentResult execute(String userId, String movieSlug, int episodeNumber);
}
