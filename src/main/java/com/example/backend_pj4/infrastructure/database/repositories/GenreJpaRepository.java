package com.example.backend_pj4.infrastructure.database.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend_pj4.infrastructure.database.entities.GenreJpaEntity;

public interface GenreJpaRepository extends JpaRepository<GenreJpaEntity, String> {
    Optional<GenreJpaEntity> findByName(String name);
    boolean existsByName(String name);
    List<GenreJpaEntity> findByNameContainingIgnoreCase(String name);
}


