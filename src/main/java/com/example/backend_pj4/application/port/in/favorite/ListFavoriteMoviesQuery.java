package com.example.backend_pj4.application.port.in.favorite;

import java.util.Set;

import com.example.backend_pj4.application.dto.favorite.FavoriteMovieResult;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.repository.filter.FavoriteFilter;

/** Cổng ĐỌC danh sách phim yêu thích. */
public interface ListFavoriteMoviesQuery {

    // Sắp xếp theo cột của chính bảng favorite; muốn sắp theo tên phim thì Sort phải trỏ vào
    // alias của JOIN, không dùng chung whitelist phẳng này được.
    Set<String> SORTABLE = Set.of("createdAt");
    String DEFAULT_SORT = "createdAt";

    PageResult<FavoriteMovieResult> execute(FavoriteFilter filter, PageQuery query);
}
