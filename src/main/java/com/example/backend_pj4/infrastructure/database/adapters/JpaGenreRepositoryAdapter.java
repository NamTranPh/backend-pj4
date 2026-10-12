package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.Genre;
import com.example.backend_pj4.domain.repository.GenreRepository;
import com.example.backend_pj4.domain.repository.filter.GenreFilter;
import com.example.backend_pj4.infrastructure.database.mappers.GenrePersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.GenreJpaRepository;

@Repository
public class JpaGenreRepositoryAdapter implements GenreRepository {

    private final GenreJpaRepository genreJpaRepository;
    private final GenrePersistenceMapper genrePersistenceMapper;

    public JpaGenreRepositoryAdapter(GenreJpaRepository genreJpaRepository,
                                     GenrePersistenceMapper genrePersistenceMapper) {
        this.genreJpaRepository = genreJpaRepository;
        this.genrePersistenceMapper = genrePersistenceMapper;
    }

    @Override
    public Genre save(Genre genre) {
        return genrePersistenceMapper.toDomain(
                genreJpaRepository.save(genrePersistenceMapper.toEntity(genre)));
    }

    @Override
    public Optional<Genre> findById(String id) {
        return genreJpaRepository.findById(id).map(genrePersistenceMapper::toDomain);
    }

    @Override
    public PageResult<Genre> find(GenreFilter filter, PageQuery query) {
        return PageableMapper.toPageResult(
                genreJpaRepository.search(filter.q(), filter.status(), PageableMapper.toPageable(query)),
                genrePersistenceMapper::toDomain);
    }

    @Override
    public List<Genre> findByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return genreJpaRepository.findAllById(ids).stream()
                .map(genrePersistenceMapper::toDomain).toList();
    }

    @Override
    public Optional<Genre> findBySlug(String slug) {
        return genreJpaRepository.findBySlug(slug).map(genrePersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return genreJpaRepository.existsByName(name);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return genreJpaRepository.existsBySlug(slug);
    }

    @Override
    @Transactional
    public void removeGenreFromAllMovies(String genreId) {
        genreJpaRepository.removeGenreFromAllMovies(genreId);
    }

    @Override
    public void deleteById(String id) {
        genreJpaRepository.deleteById(id);
    }
}
