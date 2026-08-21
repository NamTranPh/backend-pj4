package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.backend_pj4.common.constants.enums.MovieType;
import com.example.backend_pj4.common.constants.enums.VideoStatus;
import com.example.backend_pj4.common.constants.enums.VideoVisibility;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.infrastructure.database.mappers.MoviePersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.MovieJpaRepository;
import com.example.backend_pj4.infrastructure.database.specifications.MovieSpecification;

@Repository
public class JpaMovieRepositoryAdapter implements MovieRepository {

    private final MovieJpaRepository movieJpaRepository;
    private final MoviePersistenceMapper moviePersistenceMapper;

    public JpaMovieRepositoryAdapter(MovieJpaRepository movieJpaRepository, MoviePersistenceMapper moviePersistenceMapper) {
        this.movieJpaRepository = movieJpaRepository;
        this.moviePersistenceMapper = moviePersistenceMapper;
    }

    @Override
    public Movie save(Movie movie) {
        return moviePersistenceMapper.toDomain(movieJpaRepository.save(moviePersistenceMapper.toEntity(movie)));
    }

    @Override
    public Optional<Movie> findById(String id) {
        return movieJpaRepository.findById(id).map(moviePersistenceMapper::toDomain);
    }

    @Override
    public Optional<Movie> findBySlug(String slug) {
        return movieJpaRepository.findBySlug(slug).map(moviePersistenceMapper::toDomain);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return movieJpaRepository.existsBySlug(slug);
    }

    @Override
    public Optional<Movie> findByIdIncludingDeleted(String id) {
        return movieJpaRepository.findByIdIncludingDeleted(id).map(moviePersistenceMapper::toDomain);
    }

    @Override
    public List<Movie> findAll() {
        return movieJpaRepository.findAll().stream().map(moviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Page<Movie> findAll(Pageable pageable) {
        return movieJpaRepository.findAll(pageable).map(moviePersistenceMapper::toDomain);
    }

    @Override
    public Page<Movie> findAllFiltered(String search, MovieType movieType, VideoStatus status,
                                        String genreId, Integer year, String country,
                                        boolean publicOnly, Pageable pageable) {
        return movieJpaRepository.findAll(
                MovieSpecification.buildFilter(search, movieType, status, genreId, year, country, publicOnly),
                pageable
        ).map(moviePersistenceMapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        movieJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return movieJpaRepository.existsById(id);
    }

    @Override
    public List<Movie> findByTitleContaining(String title) {
        return movieJpaRepository.findByTitleContainingIgnoreCase(title).stream().map(moviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByMovieType(MovieType movieType) {
        return movieJpaRepository.findByMovieType(movieType).stream().map(moviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByStatus(VideoStatus status) {
        return movieJpaRepository.findByStatus(status).stream().map(moviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByVisibility(VideoVisibility visibility) {
        return movieJpaRepository.findByVisibility(visibility).stream().map(moviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByReleaseYear(Integer year) {
        return movieJpaRepository.findByReleaseYear(year).stream().map(moviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByReleaseYearBetween(Integer startYear, Integer endYear) {
        return movieJpaRepository.findByReleaseYearBetween(startYear, endYear).stream().map(moviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByCountry(String country) {
        return movieJpaRepository.findByCountry(country).stream().map(moviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByLanguage(String language) {
        return movieJpaRepository.findByLanguage(language).stream().map(moviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Movie> findPremiumMovies() {
        return movieJpaRepository.findByIsPremiumTrue().stream().map(moviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Movie> findFeaturedMovies() {
        return movieJpaRepository.findByIsFeaturedTrue().stream().map(moviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Movie> findFreeMovies() {
        return movieJpaRepository.findByIsPremiumFalse().stream().map(moviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Movie> findTopRated(int limit) {
        return movieJpaRepository.findTopRated(PageRequest.of(0, limit)).stream().map(moviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Movie> findMostViewed(int limit) {
        return movieJpaRepository.findMostViewed(PageRequest.of(0, limit)).stream().map(moviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByGenre(String genreId) {
        return movieJpaRepository.findByGenreId(genreId).stream().map(moviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByGenres(List<String> genreIds) {
        return movieJpaRepository.findByGenreIds(genreIds).stream().map(moviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByCreatedBy(String userId) {
        return movieJpaRepository.findByCreatedBy_Id(userId).stream().map(moviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public long countByMovieType(MovieType movieType) {
        return movieJpaRepository.countByMovieType(movieType);
    }

    @Override
    public long countByStatus(VideoStatus status) {
        return movieJpaRepository.countByStatus(status);
    }

    @Override
    public void softDeleteEpisodesByMovieId(String movieId) {
        movieJpaRepository.softDeleteEpisodesByMovieId(movieId);
    }

    @Override
    public void restoreEpisodesByMovieId(String movieId) {
        movieJpaRepository.restoreEpisodesByMovieId(movieId);
    }
}


