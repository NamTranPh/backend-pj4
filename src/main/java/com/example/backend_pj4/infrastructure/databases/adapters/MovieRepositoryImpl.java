package com.example.backend_pj4.infrastructure.databases.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.common.enums.MovieStatus;
import com.example.backend_pj4.common.enums.MovieType;
import com.example.backend_pj4.domain.entities.Movie;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.infrastructure.databases.entities.GenreEntity;
import com.example.backend_pj4.infrastructure.databases.mapper.MovieMapper;
import com.example.backend_pj4.infrastructure.databases.repository.JpaMovieRepository;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MovieRepositoryImpl implements MovieRepository {

    private final JpaMovieRepository jpaMovieRepository;
    private final MovieMapper movieMapper;
    private final EntityManager entityManager;

    // ---------------- Basic CRUD ----------------
    @Override
    @Transactional
    public Movie save(Movie movie) {
        var entity = movieMapper.toEntity(movie);

        if (movie.getGenres() != null && !movie.getGenres().isEmpty()) {
            var genreIds = movie.getGenres()
                    .stream()
                    .map(g -> g.getGenreId())
                    .toList();

            var managedGenres = entityManager.createQuery(
                    "SELECT g FROM GenreEntity g WHERE g.genreId IN :ids", GenreEntity.class)
                    .setParameter("ids", genreIds)
                    .getResultList();

            entity.setGenres(managedGenres);
        }

        var saved = jpaMovieRepository.save(entity);
        return movieMapper.toDomain(saved);
    }

    @Override
    public Optional<Movie> findById(String movieId) {
        return jpaMovieRepository.findById(movieId)
                .map(movieMapper::toDomain);
    }

    @Override
    public List<Movie> findAll() {
        return jpaMovieRepository.findAll().stream()
                .map(movieMapper::toDomain)
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
                .stream().map(movieMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByDirectorContaining(String director) {
        return jpaMovieRepository.findByDirectorContainingIgnoreCase(director)
                .stream().map(movieMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByActorsContaining(String actor) {
        return jpaMovieRepository.findByActorsContainingIgnoreCase(actor)
                .stream().map(movieMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findBySearchQuery(String query) {
        return jpaMovieRepository.findByTitleContainingIgnoreCase(query)
                .stream()
                .map(movieMapper::toDomain)
                .collect(Collectors.toList());
    }

    // ---------------- Filter queries ----------------
    @Override
    public List<Movie> findByMovieType(MovieType movieType) {
        return jpaMovieRepository.findByMovieType(movieType)
                .stream()
                .map(movieMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByStatus(MovieStatus status) {
        return jpaMovieRepository.findByStatus(status)
                .stream()
                .map(movieMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByReleaseYear(Integer year) {
        return jpaMovieRepository.findByReleaseYear(year)
                .stream().map(movieMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByReleaseYearBetween(Integer startYear, Integer endYear) {
        return jpaMovieRepository.findByReleaseYearBetween(startYear, endYear)
                .stream().map(movieMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByCountry(String country) {
        return jpaMovieRepository.findByCountryIgnoreCase(country)
                .stream().map(movieMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByLanguage(String language) {
        return jpaMovieRepository.findByLanguageIgnoreCase(language)
                .stream().map(movieMapper::toDomain)
                .collect(Collectors.toList());
    }

    // ---------------- Premium & Featured ----------------
    @Override
    public List<Movie> findPremiumMovies() {
        return jpaMovieRepository.findByIsPremiumTrue()
                .stream().map(movieMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findFeaturedMovies() {
        return jpaMovieRepository.findByIsFeaturedTrue()
                .stream().map(movieMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findFreeMovies() {
        return jpaMovieRepository.findByIsPremiumFalse()
                .stream().map(movieMapper::toDomain)
                .collect(Collectors.toList());
    }

    // ---------------- Rating & Popularity ----------------
    @Override
    public List<Movie> findTopRated(int limit) {
        return jpaMovieRepository.findTopRated(limit)
                .stream().map(movieMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findMostViewed(int limit) {
        return jpaMovieRepository.findMostViewed(limit)
                .stream().map(movieMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByRatingGreaterThan(Double rating) {
        return jpaMovieRepository.findByRatingGreaterThan(
                java.math.BigDecimal.valueOf(rating))
                .stream().map(movieMapper::toDomain)
                .collect(Collectors.toList());
    }

    // ---------------- Genre-based ----------------
    @Override
    public List<Movie> findByGenre(String genreId) {
        return jpaMovieRepository.findByGenre(genreId)
                .stream().map(movieMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByGenres(List<String> genreIds) {
        return jpaMovieRepository.findByGenres(genreIds)
                .stream().map(movieMapper::toDomain)
                .collect(Collectors.toList());
    }

    // ---------------- User-specific ----------------
    @Override
    public List<Movie> findByCreatedBy(String userId) {
        return jpaMovieRepository.findByCreatedBy(userId)
                .stream().map(movieMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findActiveMovies() {
        return jpaMovieRepository.findByIsActiveTrue()
                .stream().map(movieMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findInactiveMovies() {
        return jpaMovieRepository.findByIsActiveFalse()
                .stream().map(movieMapper::toDomain)
                .collect(Collectors.toList());
    }

    // ---------------- Statistics ----------------
    @Override
    public long countByMovieType(MovieType movieType) {
        return jpaMovieRepository.countByMovieType(movieType);
    }

    @Override
    public long countByStatus(MovieStatus status) {
        return jpaMovieRepository.countByStatus(status);
    }

    @Override
    public long countActiveMovies() {
        return jpaMovieRepository.countByIsActiveTrue();
    }
}
