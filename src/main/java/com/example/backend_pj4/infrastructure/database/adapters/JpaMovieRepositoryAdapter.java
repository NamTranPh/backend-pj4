package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.domain.repository.filter.MovieFilter;
import com.example.backend_pj4.infrastructure.database.entities.MovieJpaEntity;
import com.example.backend_pj4.infrastructure.database.mappers.MoviePersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.MovieJpaRepository;
import com.example.backend_pj4.infrastructure.database.specifications.MovieSpecification;

@Repository
public class JpaMovieRepositoryAdapter implements MovieRepository {

    private final MovieJpaRepository movieJpaRepository;
    private final MoviePersistenceMapper moviePersistenceMapper;

    public JpaMovieRepositoryAdapter(MovieJpaRepository movieJpaRepository,
                                     MoviePersistenceMapper moviePersistenceMapper) {
        this.movieJpaRepository = movieJpaRepository;
        this.moviePersistenceMapper = moviePersistenceMapper;
    }

    @Override
    public Movie save(Movie movie) {
        return moviePersistenceMapper.toDomain(
                movieJpaRepository.save(moviePersistenceMapper.toEntity(movie)));
    }

    @Override
    public Optional<Movie> findById(String id) {
        return movieJpaRepository.findById(id).map(moviePersistenceMapper::toDomain);
    }

    @Override
    public List<Movie> findByIds(Collection<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return movieJpaRepository.findAllById(ids).stream()
                .map(moviePersistenceMapper::toDomain).toList();
    }

    @Override
    public Optional<Movie> findBySlug(String slug) {
        return movieJpaRepository.findBySlug(slug).map(moviePersistenceMapper::toDomain);
    }

    @Override
    public PageResult<Movie> find(MovieFilter filter, PageQuery query) {
        return PageableMapper.toPageResult(
                movieJpaRepository.findAll(
                        MovieSpecification.buildFilter(filter.q(), filter.movieType(), filter.status(),
                                filter.genreId(), filter.year(), filter.country(), filter.publicOnly()),
                        PageableMapper.toPageable(query)),
                // toListDomain, không phải toDomain: DTO danh sách không dùng episodes và
                // createdBy, nạp về là 30 truy vấn thừa cho một trang 15 phim
                moviePersistenceMapper::toListDomain);
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
    public void softDeleteEpisodesByMovieId(String movieId) {
        movieJpaRepository.softDeleteEpisodesByMovieId(movieId);
    }

    @Override
    public void restoreEpisodesByMovieId(String movieId) {
        movieJpaRepository.restoreEpisodesByMovieId(movieId);
    }

    @Override
    public List<Movie> findRelatedMovies(String currentMovieId, List<String> genreIds, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<MovieJpaEntity> entities;
        if (genreIds != null && !genreIds.isEmpty()) {
            entities = new ArrayList<>(movieJpaRepository.findRelatedByGenres(currentMovieId, genreIds, pageable));
            if (entities.size() < limit) {
                List<MovieJpaEntity> top = movieJpaRepository.findTopPublicMovies(currentMovieId, pageable);
                for (MovieJpaEntity m : top) {
                    if (entities.size() >= limit) break;
                    if (!entities.contains(m)) {
                        entities.add(m);
                    }
                }
            }
        } else {
            entities = movieJpaRepository.findTopPublicMovies(currentMovieId, pageable);
        }
        return entities.stream().map(moviePersistenceMapper::toListDomain).toList();
    }

    @Override
    public void deleteById(String id) {
        movieJpaRepository.deleteById(id);
    }
}
