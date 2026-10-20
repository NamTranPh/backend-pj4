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
    PageResult<Movie> find(MovieFilter filter, PageQuery query);
    Optional<Movie> findById(String id);
    List<Movie> findByIds(Collection<String> ids);
    Optional<Movie> findBySlug(String slug);
    boolean existsBySlug(String slug);
    /** Lấy cả phim đã soft-delete, dùng cho luồng khôi phục - Admin */
    Optional<Movie> findByIdIncludingDeleted(String id);
    void softDeleteEpisodesByMovieId(String movieId);
    void restoreEpisodesByMovieId(String movieId);
    void deleteById(String id);
}
