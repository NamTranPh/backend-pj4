package com.example.backend_pj4.application.port.in.episode;

import java.util.Set;

import com.example.backend_pj4.application.dto.episode.PublicEpisodeResult;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;

/**
 * Cổng ĐỌC danh sách tập phim cho người xem, tra cứu bằng <b>slug</b> thay vì id.
 *
 * <p>Tách riêng khỏi {@link ListEpisodesByMovieQuery} (dùng cho CMS) vì khác cả đầu vào
 * (slug / id) lẫn đầu ra: bản công khai không trả rawFileKey, masterPlaylistKey hay viewCount.
 */
public interface ListPublicEpisodesQuery {

    Set<String> SORTABLE = ListEpisodesByMovieQuery.SORTABLE;
    String DEFAULT_SORT = ListEpisodesByMovieQuery.DEFAULT_SORT;

    PageResult<PublicEpisodeResult> execute(String movieSlug, String search, PageQuery query);
}
