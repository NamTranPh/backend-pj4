package com.example.backend_pj4.infrastructure.database.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend_pj4.common.constants.enums.GenreStatus;
import com.example.backend_pj4.infrastructure.database.entities.GenreJpaEntity;

public interface GenreJpaRepository extends JpaRepository<GenreJpaEntity, String> {
    Optional<GenreJpaEntity> findByName(String name);
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
    Optional<GenreJpaEntity> findBySlug(String slug);
    List<GenreJpaEntity> findByNameContainingIgnoreCase(String name);
    Page<GenreJpaEntity> findByStatus(GenreStatus status, Pageable pageable);
    Page<GenreJpaEntity> findByNameContainingIgnoreCaseAndStatus(String name, GenreStatus status, Pageable pageable);
    Page<GenreJpaEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Modifying
    @Query(value = "DELETE FROM movie_genre WHERE genre_id = :genreId", nativeQuery = true)
    void removeGenreFromAllMovies(@Param("genreId") String genreId);
}
