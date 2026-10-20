package com.example.backend_pj4.infrastructure.database.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend_pj4.infrastructure.database.entities.RatingJpaEntity;

public interface RatingJpaRepository
        extends JpaRepository<RatingJpaEntity, String>, JpaSpecificationExecutor<RatingJpaEntity> {

    Optional<RatingJpaEntity> findByUser_IdAndMovie_Id(String userId, String movieId);

    /** Xoá <b>cứng</b>: xem javadoc của {@code RatingRepository.deleteByUserIdAndMovieId}. */
    void deleteByUser_IdAndMovie_Id(String userId, String movieId);

    /** Trả null khi phim chưa có ai đánh giá — caller phải xử lý. */
    @Query("SELECT AVG(r.score) FROM RatingJpaEntity r WHERE r.movie.id = :movieId")
    Double calculateAverageByMovieId(@Param("movieId") String movieId);

    long countByMovie_Id(String movieId);

    @Query("SELECT r.movie.id, COUNT(r.id) FROM RatingJpaEntity r WHERE r.movie.id IN :movieIds GROUP BY r.movie.id")
    List<Object[]> countGroupedByMovieIds(@Param("movieIds") Collection<String> movieIds);
}
