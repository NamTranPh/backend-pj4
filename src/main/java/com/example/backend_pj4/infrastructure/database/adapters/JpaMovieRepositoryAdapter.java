package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.example.backend_pj4.common.constants.enums.MovieType;
import com.example.backend_pj4.common.constants.enums.VideoStatus;
import com.example.backend_pj4.common.constants.enums.VideoVisibility;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.infrastructure.database.mappers.MoviePersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.SpringDataMovieRepository;

@Repository
public class JpaMovieRepositoryAdapter implements MovieRepository {

    private final SpringDataMovieRepository SpringDataMovieRepository;
    private final MoviePersistenceMapper MoviePersistenceMapper;

    public JpaMovieRepositoryAdapter(SpringDataMovieRepository SpringDataMovieRepository, MoviePersistenceMapper MoviePersistenceMapper) {
        this.SpringDataMovieRepository = SpringDataMovieRepository;
        this.MoviePersistenceMapper = MoviePersistenceMapper;
    }

    @Override
    public Movie save(Movie movie) {
        return MoviePersistenceMapper.toDomain(SpringDataMovieRepository.save(MoviePersistenceMapper.toEntity(movie)));
    }

    @Override
    public Optional<Movie> findById(String id) {
        return SpringDataMovieRepository.findById(id).map(MoviePersistenceMapper::toDomain);
    }

    @Override
    public List<Movie> findAll() {
        return SpringDataMovieRepository.findAll().stream().map(MoviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        SpringDataMovieRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return SpringDataMovieRepository.existsById(id);
    }

    @Override
    public List<Movie> findByTitleContaining(String title) {
        return SpringDataMovieRepository.findByTitleContainingIgnoreCase(title).stream().map(MoviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByMovieType(MovieType movieType) {
        return SpringDataMovieRepository.findByMovieType(movieType).stream().map(MoviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByStatus(VideoStatus status) {
        return SpringDataMovieRepository.findByStatus(status).stream().map(MoviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByVisibility(VideoVisibility visibility) {
        return SpringDataMovieRepository.findByVisibility(visibility).stream().map(MoviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByReleaseYear(Integer year) {
        return SpringDataMovieRepository.findByReleaseYear(year).stream().map(MoviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByReleaseYearBetween(Integer startYear, Integer endYear) {
        return SpringDataMovieRepository.findByReleaseYearBetween(startYear, endYear).stream().map(MoviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByCountry(String country) {
        return SpringDataMovieRepository.findByCountry(country).stream().map(MoviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByLanguage(String language) {
        return SpringDataMovieRepository.findByLanguage(language).stream().map(MoviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Movie> findPremiumMovies() {
        return SpringDataMovieRepository.findByIsPremiumTrue().stream().map(MoviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Movie> findFeaturedMovies() {
        return SpringDataMovieRepository.findByIsFeaturedTrue().stream().map(MoviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Movie> findFreeMovies() {
        return SpringDataMovieRepository.findByIsPremiumFalse().stream().map(MoviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Movie> findTopRated(int limit) {
        return SpringDataMovieRepository.findTopRated(PageRequest.of(0, limit)).stream().map(MoviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Movie> findMostViewed(int limit) {
        return SpringDataMovieRepository.findMostViewed(PageRequest.of(0, limit)).stream().map(MoviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByGenre(String genreId) {
        return SpringDataMovieRepository.findByGenreId(genreId).stream().map(MoviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByGenres(List<String> genreIds) {
        return SpringDataMovieRepository.findByGenreIds(genreIds).stream().map(MoviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByCreatedBy(String userId) {
        return SpringDataMovieRepository.findByCreatedBy_Id(userId).stream().map(MoviePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public long countByMovieType(MovieType movieType) {
        return SpringDataMovieRepository.countByMovieType(movieType);
    }

    @Override
    public long countByStatus(VideoStatus status) {
        return SpringDataMovieRepository.countByStatus(status);
    }
}


