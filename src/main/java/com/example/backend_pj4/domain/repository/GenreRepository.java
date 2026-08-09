package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

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
}
