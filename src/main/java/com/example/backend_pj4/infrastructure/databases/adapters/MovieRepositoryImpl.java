package com.example.backend_pj4.infrastructure.databases.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.domain.entities.Movie;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.infrastructure.databases.mapper.MovieMapper;
import com.example.backend_pj4.infrastructure.databases.repository.JpaMovieRepository;

@Repository
public class MovieRepositoryImpl implements MovieRepository {
    private final JpaMovieRepository jpaMovieRepository;

    public MovieRepositoryImpl(JpaMovieRepository jpaMovieRepository) {
        this.jpaMovieRepository = jpaMovieRepository;
    }

    // ---------------- Basic CRUD ----------------
    @Override
    public Movie save(Movie movie) {
        var entity = MovieMapper.toEntity(movie);
        var savedEntity = jpaMovieRepository.save(entity);
        return MovieMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Movie> findById(String movieId) {
        return jpaMovieRepository.findById(movieId)
                .map(MovieMapper::toDomain);
    }

    @Override
    public List<Movie> findAll() {
        return jpaMovieRepository.findAll().stream()
                .map(MovieMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String movieId) {
        jpaMovieRepository.deleteById(movieId);
    }

    @Override
    public boolean existsById(String movieId) {
        return jpaMovieRepository.existsById(movieId);
    }

    // ---------------- Search queries ----------------
    @Override
    public List<Movie> findByTitleContaining(String title) {
        return jpaMovieRepository.findByTitleContainingIgnoreCase(title)
                .stream().map(MovieMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByDirectorContaining(String director) {
        return jpaMovieRepository.findByDirectorContainingIgnoreCase(director)
                .stream().map(MovieMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByActorsContaining(String actor) {
        return jpaMovieRepository.findByActorsContainingIgnoreCase(actor)
                .stream().map(MovieMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findBySearchQuery(String query) {
        return jpaMovieRepository.findByTitleContainingIgnoreCase(query)
                .stream().map(MovieMapper::toDomain)
                .collect(Collectors.toList());
    }

    // ---------------- Filter queries ----------------
    @Override
    public List<Movie> findByMovieType(String movieType) {
        return jpaMovieRepository.findByMovieType(
                com.example.backend_pj4.domain.enums.MovieType.valueOf(movieType))
                .stream().map(MovieMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByStatus(String status) {
        return jpaMovieRepository.findByStatus(
                com.example.backend_pj4.domain.enums.MovieStatus.valueOf(status))
                .stream().map(MovieMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByReleaseYear(Integer year) {
        return jpaMovieRepository.findByReleaseYear(year)
                .stream().map(MovieMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByReleaseYearBetween(Integer startYear, Integer endYear) {
        return jpaMovieRepository.findByReleaseYearBetween(startYear, endYear)
                .stream().map(MovieMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByCountry(String country) {
        return jpaMovieRepository.findByCountryIgnoreCase(country)
                .stream().map(MovieMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByLanguage(String language) {
        return jpaMovieRepository.findByLanguageIgnoreCase(language)
                .stream().map(MovieMapper::toDomain)
                .collect(Collectors.toList());
    }

    // ---------------- Premium & Featured ----------------
    @Override
    public List<Movie> findPremiumMovies() {
        return jpaMovieRepository.findByIsPremiumTrue()
                .stream().map(MovieMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findFeaturedMovies() {
        return jpaMovieRepository.findByIsFeaturedTrue()
                .stream().map(MovieMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findFreeMovies() {
        return jpaMovieRepository.findByIsPremiumFalse()
                .stream().map(MovieMapper::toDomain)
                .collect(Collectors.toList());
    }

    // ---------------- Rating & Popularity ----------------
    @Override
    public List<Movie> findTopRated(int limit) {
        return jpaMovieRepository.findTopRated(limit)
                .stream().map(MovieMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findMostViewed(int limit) {
        return jpaMovieRepository.findMostViewed(limit)
                .stream().map(MovieMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByRatingGreaterThan(Double rating) {
        return jpaMovieRepository.findByRatingGreaterThan(
                java.math.BigDecimal.valueOf(rating))
                .stream().map(MovieMapper::toDomain)
                .collect(Collectors.toList());
    }

    // ---------------- Genre-based ----------------
    @Override
    public List<Movie> findByGenre(String genreId) {
        return jpaMovieRepository.findByGenre(genreId)
                .stream().map(MovieMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByGenres(List<String> genreIds) {
        return jpaMovieRepository.findByGenres(genreIds)
                .stream().map(MovieMapper::toDomain)
                .collect(Collectors.toList());
    }

    // ---------------- User-specific ----------------
    @Override
    public List<Movie> findByCreatedBy(String userId) {
        return jpaMovieRepository.findByCreatedBy(userId)
                .stream().map(MovieMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findActiveMovies() {
        return jpaMovieRepository.findByIsActiveTrue()
                .stream().map(MovieMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findInactiveMovies() {
        return jpaMovieRepository.findByIsActiveFalse()
                .stream().map(MovieMapper::toDomain)
                .collect(Collectors.toList());
    }

    // ---------------- Statistics ----------------
    @Override
    public long countByMovieType(String movieType) {
        return jpaMovieRepository.countByMovieType(
                com.example.backend_pj4.domain.enums.MovieType.valueOf(movieType));
    }

    @Override
    public long countByStatus(String status) {
        return jpaMovieRepository.countByStatus(
                com.example.backend_pj4.domain.enums.MovieStatus.valueOf(status));
    }

    @Override
    public long countActiveMovies() {
        return jpaMovieRepository.countByIsActiveTrue();
    }
}
