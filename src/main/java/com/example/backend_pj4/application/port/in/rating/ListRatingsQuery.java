package com.example.backend_pj4.application.port.in.rating;

import java.util.Set;

import com.example.backend_pj4.application.dto.rating.RatingResult;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;

/** Cong DOC danh sach danh gia kem nhan xet cua mot phim. */
public interface ListRatingsQuery {

    Set<String> SORTABLE = Set.of("createdAt", "score");
    String DEFAULT_SORT = "createdAt";

    PageResult<RatingResult> execute(String movieSlug, PageQuery query);
}
