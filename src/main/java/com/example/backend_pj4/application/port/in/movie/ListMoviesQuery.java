package com.example.backend_pj4.application.port.in.movie;

import java.util.Set;

import com.example.backend_pj4.application.dto.movie.MovieResult;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.repository.filter.MovieFilter;

/** Cổng ĐỌC danh sách phim. */
public interface ListMoviesQuery {

    Set<String> SORTABLE = Set.of("createdAt", "title", "viewCount", "rating", "releaseYear");
    String DEFAULT_SORT = "createdAt";

    PageResult<MovieResult> execute(MovieFilter filter, PageQuery query);
}
