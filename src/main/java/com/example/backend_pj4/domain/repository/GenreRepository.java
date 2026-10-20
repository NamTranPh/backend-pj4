package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.Genre;
import com.example.backend_pj4.domain.repository.filter.GenreFilter;

public interface GenreRepository {
    Genre save(Genre genre);
    PageResult<Genre> find(GenreFilter filter, PageQuery query);
    Optional<Genre> findById(String id);
    List<Genre> findByIds(List<String> ids);
    Optional<Genre> findBySlug(String slug);
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
    void removeGenreFromAllMovies(String genreId);
    void deleteById(String id);
}
