package com.example.backend_pj4.application.port.in.comment;

import java.util.Set;

import com.example.backend_pj4.application.dto.comment.CommentResult;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;

/**
 * Cong DOC cay binh luan cong khai duoi mot phim hoac mot tap.
 *
 * <p>Phan trang theo binh luan <b>goc</b>: {@code totalItems} la so luong, khong phai tong so
 * binh luan. Con chau cua cac goc trong trang duoc nap het va long vao {@code replies}.
 *
 * <p>Nhan slug va episodeId roi tu dung {@code CommentFilter} ben trong handler, khong de
 * controller dung filter: ba co {@code rootOnly}, {@code isApproved}, {@code episodeScoped} la
 * thu quyet dinh endpoint nay co lo binh luan dang bi an hay khong. De controller truyen vao thi
 * truyen nham khong gay loi bien dich (AGENTS.md §28.2).
 */
public interface ListCommentsQuery {

    // likeCount sap xep duoc de FE lam tab "noi bat nhat"; day la cot cua chinh bang comment nen
    // khong phai JOIN.
    Set<String> SORTABLE = Set.of("createdAt", "likeCount");
    String DEFAULT_SORT = "createdAt";

    /**
     * @param episodeId null nghia la binh luan cap phim
     * @param viewerId  nguoi dang xem, de bat co {@code isLiked}; null khi goi khong token
     */
    PageResult<CommentResult> execute(String movieSlug, String episodeId, PageQuery query, String viewerId);
}
