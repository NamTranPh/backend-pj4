package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.common.constants.enums.MovieType;
import com.example.backend_pj4.common.constants.enums.VideoStatus;
import com.example.backend_pj4.common.constants.enums.VideoVisibility;
import com.example.backend_pj4.domain.model.Movie;

public interface MovieRepository {
    Movie save(Movie movie);
    Optional<Movie> findById(String id);
    List<Movie> findAll();
    void deleteById(String id);
    boolean existsById(String id);
    List<Movie> findByTitleContaining(String title);
    List<Movie> findByMovieType(MovieType movieType);
    List<Movie> findByStatus(VideoStatus status);
    List<Movie> findByVisibility(VideoVisibility visibility);
    List<Movie> findByReleaseYear(Integer year);
    List<Movie> findByReleaseYearBetween(Integer startYear, Integer endYear);
    List<Movie> findByCountry(String country);
    List<Movie> findByLanguage(String language);
    List<Movie> findPremiumMovies();
    List<Movie> findFeaturedMovies();
    List<Movie> findFreeMovies();
    List<Movie> findTopRated(int limit);
    List<Movie> findMostViewed(int limit);
    List<Movie> findByGenre(String genreId);
    List<Movie> findByGenres(List<String> genreIds);
    List<Movie> findByCreatedBy(String userId);
    long countByMovieType(MovieType movieType);
    long countByStatus(VideoStatus status);
}
