package com.example.backend_pj4.application.query.episode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.episode.EpisodeResult;
import com.example.backend_pj4.application.mapper.EpisodeResultMapper;
import com.example.backend_pj4.application.port.in.episode.ListEpisodesByMovieQuery;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.repository.EpisodeRepository;
import com.example.backend_pj4.domain.repository.filter.EpisodeFilter;

@Service
public class ListEpisodesByMovieQueryHandler implements ListEpisodesByMovieQuery {

    private final EpisodeRepository episodeRepository;
    private final EpisodeResultMapper episodeResultMapper;

    public ListEpisodesByMovieQueryHandler(EpisodeRepository episodeRepository,
                                           EpisodeResultMapper episodeResultMapper) {
        this.episodeRepository = episodeRepository;
        this.episodeResultMapper = episodeResultMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<EpisodeResult> execute(EpisodeFilter filter, PageQuery query) {
        return episodeRepository.find(filter, query).map(episodeResultMapper::toResult);
    }
}
