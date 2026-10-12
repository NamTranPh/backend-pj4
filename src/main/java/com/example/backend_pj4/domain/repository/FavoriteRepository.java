package com.example.backend_pj4.domain.repository;

import java.util.Optional;

import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.ListFavorite;
import com.example.backend_pj4.domain.repository.filter.FavoriteFilter;

public interface FavoriteRepository {
    ListFavorite save(ListFavorite favorite);

    Optional<ListFavorite> findById(String id);

    /** Phim yêu thích của một người, lọc theo tên phim, có sắp xếp và phân trang. */
    PageResult<ListFavorite> find(FavoriteFilter filter, PageQuery query);

    boolean existsByUserIdAndMovieId(String userId, String movieId);

    void deleteByUserIdAndMovieId(String userId, String movieId);

    void deleteById(String id);
}
