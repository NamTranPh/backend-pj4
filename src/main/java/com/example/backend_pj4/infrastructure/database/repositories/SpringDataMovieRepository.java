package com.example.backend_pj4.infrastructure.database.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend_pj4.common.constants.enums.MovieType;
import com.example.backend_pj4.common.constants.enums.VideoStatus;
import com.example.backend_pj4.common.constants.enums.VideoVisibility;
import com.example.backend_pj4.infrastructure.database.entities.MovieJpaEntity;

public interface SpringDataMovieRepository extends JpaRepository<MovieJpaEntity, String> {
    List<MovieJpaEntity> findByTitleContainingIgnoreCase(String title);
    List<MovieJpaEntity> findByMovieType(MovieType movieType);
    List<MovieJpaEntity> findByStatus(VideoStatus status);
    List<MovieJpaEntity> findByVisibility(VideoVisibility visibility);
    List<MovieJpaEntity> findByReleaseYear(Integer year);
    List<MovieJpaEntity> findByReleaseYearBetween(Integer startYear, Integer endYear);
    List<MovieJpaEntity> findByCountry(String country);
    List<MovieJpaEntity> findByLanguage(String language);
    List<MovieJpaEntity> findByIsPremiumTrue();
    List<MovieJpaEntity> findByIsFeaturedTrue();
    List<MovieJpaEntity> findByIsPremiumFalse();

    @Query("SELECT m FROM MovieJpaEntity m ORDER BY m.rating DESC NULLS LAST")
    List<MovieJpaEntity> findTopRated(Pageable pageable);

    @Query("SELECT m FROM MovieJpaEntity m ORDER BY m.viewCount DESC NULLS LAST")
    List<MovieJpaEntity> findMostViewed(Pageable pageable);

    @Query("SELECT m FROM MovieJpaEntity m JOIN m.genres g WHERE g.id = :genreId")
    List<MovieJpaEntity> findByGenreId(@Param("genreId") String genreId);

    @Query("SELECT DISTINCT m FROM MovieJpaEntity m JOIN m.genres g WHERE g.id IN :genreIds")
    List<MovieJpaEntity> findByGenreIds(@Param("genreIds") List<String> genreIds);

    List<MovieJpaEntity> findByCreatedBy_Id(String userId);

    long countByMovieType(MovieType movieType);
    long countByStatus(VideoStatus status);
}


