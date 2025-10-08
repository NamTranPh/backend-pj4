package com.example.backend_pj4.infrastructure.databases.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend_pj4.infrastructure.databases.entities.GenreEntity;

@Repository
public interface JpaGenreRepository extends JpaRepository<GenreEntity, String> {

    Optional<GenreEntity> findByName(String name);

    boolean existsByName(String name);

    List<GenreEntity> findByNameContaining(String keyword);
}
