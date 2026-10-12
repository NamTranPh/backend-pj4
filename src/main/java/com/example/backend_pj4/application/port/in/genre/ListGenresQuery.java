package com.example.backend_pj4.application.port.in.genre;

import java.util.Set;

import com.example.backend_pj4.application.dto.genre.GenreResult;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.repository.filter.GenreFilter;

/** Cổng ĐỌC danh sách thể loại. */
public interface ListGenresQuery {

    Set<String> SORTABLE = Set.of("createdAt", "name");
    String DEFAULT_SORT = "createdAt";

    PageResult<GenreResult> execute(GenreFilter filter, PageQuery query);
}
