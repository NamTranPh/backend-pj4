package com.example.backend_pj4.infrastructure.database.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend_pj4.infrastructure.database.entities.MovieJpaEntity;

/**
 * Lọc và phân trang đến từ {@link JpaSpecificationExecutor} kết hợp
 * {@code MovieSpecification.buildFilter(...)}, nên ở đây không khai báo method tìm kiếm nào.
 */
public interface MovieJpaRepository
        extends JpaRepository<MovieJpaEntity, String>, JpaSpecificationExecutor<MovieJpaEntity> {

    Optional<MovieJpaEntity> findBySlug(String slug);

    boolean existsBySlug(String slug);

    /** Bỏ qua bộ lọc soft-delete của Hibernate, dùng cho luồng khôi phục phim. */
    @Query(value = "SELECT * FROM movie WHERE id = :id", nativeQuery = true)
    Optional<MovieJpaEntity> findByIdIncludingDeleted(@Param("id") String id);

    @Modifying
    @Query(value = "UPDATE episode SET deleted_at = NOW() WHERE movie_id = :movieId AND deleted_at IS NULL",
            nativeQuery = true)
    void softDeleteEpisodesByMovieId(@Param("movieId") String movieId);

    @Modifying
    @Query(value = "UPDATE episode SET deleted_at = NULL WHERE movie_id = :movieId AND deleted_at IS NOT NULL",
            nativeQuery = true)
    void restoreEpisodesByMovieId(@Param("movieId") String movieId);
}
