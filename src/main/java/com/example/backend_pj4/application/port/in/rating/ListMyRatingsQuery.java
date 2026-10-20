package com.example.backend_pj4.application.port.in.rating;

import java.util.Set;

import com.example.backend_pj4.application.dto.rating.MyRatingResult;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;

public interface ListMyRatingsQuery {

    Set<String> SORTABLE = Set.of("createdAt", "score");
    String DEFAULT_SORT = "createdAt";

    PageResult<MyRatingResult> execute(String userId, PageQuery query);
}
