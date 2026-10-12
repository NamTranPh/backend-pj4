package com.example.backend_pj4.domain.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.filter.MovieFilter;

public interface MovieRepository {
    Movie save(Movie movie);

    Optional<Movie> findById(String id);

    /** Nạp hàng loạt theo danh sách id, thay cho việc gọi findById trong vòng lặp (N+1). */
    List<Movie> findByIds(Collection<String> ids);

    Optional<Movie> findBySlug(String slug);

    /** Danh sách phim, có lọc, sắp xếp và phân trang. */
    PageResult<Movie> find(MovieFilter filter, PageQuery query);

    boolean existsBySlug(String slug);

    /** Lấy cả phim đã soft-delete, dùng cho luồng khôi phục. */
    Optional<Movie> findByIdIncludingDeleted(String id);

    void softDeleteEpisodesByMovieId(String movieId);

    void restoreEpisodesByMovieId(String movieId);

    void deleteById(String id);
}
