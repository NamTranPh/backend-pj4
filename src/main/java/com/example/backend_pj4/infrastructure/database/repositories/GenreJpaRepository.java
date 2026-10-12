package com.example.backend_pj4.infrastructure.database.repositories;

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

    /**
     * Thay cho 4 method riêng trước đây (findAll, findByStatus,
     * findByNameContainingIgnoreCaseAndStatus, findByNameContainingIgnoreCase).
     * Tham số null thì bỏ qua điều kiện đó.
     */
    @Query("SELECT g FROM GenreJpaEntity g WHERE (:search IS NULL OR :search = '' "
            + "OR LOWER(g.name) LIKE LOWER(CONCAT('%', :search, '%'))) "
            + "AND (:status IS NULL OR g.status = :status)")
    Page<GenreJpaEntity> search(@Param("search") String search,
                                @Param("status") GenreStatus status,
                                Pageable pageable);

    boolean existsByName(String name);

    boolean existsBySlug(String slug);

    Optional<GenreJpaEntity> findBySlug(String slug);

    @Modifying
    @Query(value = "DELETE FROM movie_genre WHERE genre_id = :genreId", nativeQuery = true)
    void removeGenreFromAllMovies(@Param("genreId") String genreId);
}
