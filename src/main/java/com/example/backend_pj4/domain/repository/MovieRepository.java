package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.infrastructure.databases.entities.Movie;

public interface MovieRepository {
    // Basic CRUD
    Movie save(Movie movie);

    Optional<Movie> findById(String movieId);

    List<Movie> findAll();

    void deleteById(String movieId);

    boolean existsById(String movieId);

    // Search queries
    List<Movie> findByTitleContaining(String title);

    List<Movie> findByDirectorContaining(String director);

    List<Movie> findByActorsContaining(String actor);

    List<Movie> findBySearchQuery(String query); // Full text search

    // Filter queries
    List<Movie> findByMovieType(String movieType);

    List<Movie> findByStatus(String status);

    List<Movie> findByReleaseYear(Integer year);

    List<Movie> findByReleaseYearBetween(Integer startYear, Integer endYear);

    List<Movie> findByCountry(String country);

    List<Movie> findByLanguage(String language);

    // Premium & Featured
    List<Movie> findPremiumMovies();

    List<Movie> findFeaturedMovies();

    List<Movie> findFreeMovies();

    // Rating & Popularity
    List<Movie> findTopRated(int limit);

    List<Movie> findMostViewed(int limit);

    List<Movie> findByRatingGreaterThan(Double rating);

    // Genre-based queries
    List<Movie> findByGenre(String genreId);

    List<Movie> findByGenres(List<String> genreIds);

    // User-specific queries
    List<Movie> findByCreatedBy(String userId);

    List<Movie> findActiveMovies();

    List<Movie> findInactiveMovies();

    // Statistics
    long countByMovieType(String movieType);

    long countByStatus(String status);

    long countActiveMovies();
}
