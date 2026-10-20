package com.example.backend_pj4.application.port.in.comment;

import java.util.Set;

import com.example.backend_pj4.application.dto.comment.MyCommentResult;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;

public interface ListMyCommentsQuery {

    Set<String> SORTABLE = Set.of("createdAt", "likeCount");
    String DEFAULT_SORT = "createdAt";

    PageResult<MyCommentResult> execute(String userId, PageQuery query);
}
