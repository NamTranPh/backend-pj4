package com.example.backend_pj4.application.port.in.episode;

import java.util.List;

import com.example.backend_pj4.application.dto.episode.EpisodeResult;

public interface ListEpisodesByMovieUseCase {
    List<EpisodeResult> execute(String movieId);
}
