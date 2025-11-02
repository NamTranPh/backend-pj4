package com.example.backend_pj4.infrastructure.databases.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.backend_pj4.common.enums.MovieStatus;
import com.example.backend_pj4.common.enums.MovieType;
import com.example.backend_pj4.infrastructure.databases.entities.MovieEntity;

@Repository
public interface JpaMovieRepository extends JpaRepository<MovieEntity, String> {
    List<MovieEntity> findByTitleContainingIgnoreCase(String title);

    List<MovieEntity> findByDirectorContainingIgnoreCase(String director);

    List<MovieEntity> findByActorsContainingIgnoreCase(String actors);

    List<MovieEntity> findByMovieType(MovieType movieType);

    List<MovieEntity> findByStatus(MovieStatus status);

    List<MovieEntity> findByReleaseYear(Integer year);

    List<MovieEntity> findByReleaseYearBetween(Integer start, Integer end);

    List<MovieEntity> findByCountryIgnoreCase(String country);

    List<MovieEntity> findByLanguageIgnoreCase(String language);

    List<MovieEntity> findByIsPremiumTrue();

    List<MovieEntity> findByIsFeaturedTrue();

    List<MovieEntity> findByIsPremiumFalse();

    List<MovieEntity> findByIsActiveTrue();

    List<MovieEntity> findByIsActiveFalse();

    List<MovieEntity> findByRatingGreaterThan(BigDecimal rating);

    // custom query: top rated
    @Query("SELECT m FROM MovieEntity m ORDER BY m.rating DESC LIMIT ?1")
    List<MovieEntity> findTopRated(int limit);

    // custom query: most viewed
    @Query("SELECT m FROM MovieEntity m ORDER BY m.viewCount DESC LIMIT ?1")
    List<MovieEntity> findMostViewed(int limit);

    // genre-based
    @Query("SELECT m FROM MovieEntity m JOIN m.genres g WHERE g.genreId = ?1")
    List<MovieEntity> findByGenre(String genreId);

    @Query("SELECT DISTINCT m FROM MovieEntity m JOIN m.genres g WHERE g.genreId IN ?1")
    List<MovieEntity> findByGenres(List<String> genreIds);

    // user-specific
    @Query("SELECT m FROM MovieEntity m WHERE m.createdBy.userId = ?1")
    List<MovieEntity> findByCreatedBy(String userId);

    // statistics
    long countByMovieType(MovieType movieType);

    long countByStatus(MovieStatus status);

    long countByIsActiveTrue();
}
