package com.example.backend_pj4.application.usecase.episode;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.episode.EpisodeResult;
import com.example.backend_pj4.application.mapper.EpisodeResultMapper;
import com.example.backend_pj4.application.port.in.episode.ListEpisodesByMovieUseCase;
import com.example.backend_pj4.domain.repository.EpisodeRepository;

@Service
public class ListEpisodesByMovieService implements ListEpisodesByMovieUseCase {

    private final EpisodeRepository episodeRepository;
    private final EpisodeResultMapper episodeResultMapper;

    public ListEpisodesByMovieService(EpisodeRepository episodeRepository, EpisodeResultMapper episodeResultMapper) {
        this.episodeRepository = episodeRepository;
        this.episodeResultMapper = episodeResultMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EpisodeResult> execute(String movieId) {
        return episodeRepository.findByMovieIdOrderByEpisodeNumber(movieId)
                .stream()
                .map(episodeResultMapper::toResult)
                .toList();
    }
}
