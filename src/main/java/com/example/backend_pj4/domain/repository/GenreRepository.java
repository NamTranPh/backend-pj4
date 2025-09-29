package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.infrastructure.databases.entities.Genre;

public interface GenreRepository {
    // Basic
    Genre save(Genre genre);

    Optional<Genre> findById(String genreId);

    List<Genre> finAll();

    void deleteById(String genreId);

    // Business queries
    Optional<Genre> findByName(String name);

    boolean existsByName(String name);

    List<Genre> findByNameContaining(String keyword);

}
