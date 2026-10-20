package com.example.backend_pj4.application.port.in.comment;

import java.util.Set;

import com.example.backend_pj4.application.dto.comment.AdminCommentResult;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;

/** Cong DOC danh sach binh luan cho man kiem duyet — phang, khong dung cay, khong cache. */
public interface ListCommentsAdminQuery {

    Set<String> SORTABLE = Set.of("createdAt", "likeCount");
    String DEFAULT_SORT = "createdAt";

    /**
     * @param movieId    null nghia la moi phim
     * @param isApproved null nghia la ca da duyet lan dang an
     * @param search     tu khoa tim trong noi dung binh luan
     */
    PageResult<AdminCommentResult> execute(String movieId, Boolean isApproved, String search, PageQuery query);
}
