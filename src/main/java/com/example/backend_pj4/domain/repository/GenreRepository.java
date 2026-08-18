package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.backend_pj4.common.constants.enums.GenreStatus;
import com.example.backend_pj4.domain.model.Genre;

public interface GenreRepository {
    Genre save(Genre genre);
    Optional<Genre> findById(String id);
    List<Genre> findAll();
    void deleteById(String id);
    Optional<Genre> findByName(String name);
    boolean existsByName(String name);
    List<Genre> findByNameContaining(String name);
    List<Genre> findAllByIds(List<String> ids);
    Optional<Genre> findBySlug(String slug);
    boolean existsBySlug(String slug);
    Page<Genre> findAll(Pageable pageable);
    Page<Genre> findByStatus(GenreStatus status, Pageable pageable);
    Page<Genre> findByNameContainingAndStatus(String name, GenreStatus status, Pageable pageable);
    Page<Genre> findByNameContaining(String name, Pageable pageable);
    void removeGenreFromAllMovies(String genreId);
}
