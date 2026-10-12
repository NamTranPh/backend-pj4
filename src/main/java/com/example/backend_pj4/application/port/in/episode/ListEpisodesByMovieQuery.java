package com.example.backend_pj4.application.port.in.episode;

import java.util.Set;

import com.example.backend_pj4.application.dto.episode.EpisodeResult;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.repository.filter.EpisodeFilter;

/** Cổng ĐỌC danh sách tập phim cho CMS. */
public interface ListEpisodesByMovieQuery {

    Set<String> SORTABLE = Set.of("episodeNumber", "createdAt", "title");
    String DEFAULT_SORT = "episodeNumber";

    PageResult<EpisodeResult> execute(EpisodeFilter filter, PageQuery query);
}
