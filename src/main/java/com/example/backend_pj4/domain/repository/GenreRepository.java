package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.domain.entities.Genre;

public interface GenreRepository {
    // Basic
    Genre save(Genre genre);

    Optional<Genre> findById(String genreId);

    List<Genre> findAll();

    void deleteById(String genreId);

    // Business queries
    Optional<Genre> findByName(String name);

    boolean existsByName(String name);

    List<Genre> findByNameContaining(String keyword);
}
