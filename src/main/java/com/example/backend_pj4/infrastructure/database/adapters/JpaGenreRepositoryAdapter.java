package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.common.constants.enums.GenreStatus;
import com.example.backend_pj4.domain.model.Genre;
import com.example.backend_pj4.domain.repository.GenreRepository;
import com.example.backend_pj4.infrastructure.database.mappers.GenrePersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.GenreJpaRepository;

@Repository
public class JpaGenreRepositoryAdapter implements GenreRepository {

    private final GenreJpaRepository genreJpaRepository;
    private final GenrePersistenceMapper genrePersistenceMapper;

    public JpaGenreRepositoryAdapter(GenreJpaRepository genreJpaRepository, GenrePersistenceMapper genrePersistenceMapper) {
        this.genreJpaRepository = genreJpaRepository;
        this.genrePersistenceMapper = genrePersistenceMapper;
    }

    @Override
    public Genre save(Genre genre) {
        return genrePersistenceMapper.toDomain(genreJpaRepository.save(genrePersistenceMapper.toEntity(genre)));
    }

    @Override
    public Optional<Genre> findById(String id) {
        return genreJpaRepository.findById(id).map(genrePersistenceMapper::toDomain);
    }

    @Override
    public List<Genre> findAll() {
        return genreJpaRepository.findAll().stream().map(genrePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        genreJpaRepository.deleteById(id);
    }

    @Override
    public Optional<Genre> findByName(String name) {
        return genreJpaRepository.findByName(name).map(genrePersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return genreJpaRepository.existsByName(name);
    }

    @Override
    public List<Genre> findByNameContaining(String name) {
        return genreJpaRepository.findByNameContainingIgnoreCase(name).stream().map(genrePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Genre> findAllByIds(List<String> ids) {
        return genreJpaRepository.findAllById(ids).stream().map(genrePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Genre> findBySlug(String slug) {
        return genreJpaRepository.findBySlug(slug).map(genrePersistenceMapper::toDomain);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return genreJpaRepository.existsBySlug(slug);
    }

    @Override
    public Page<Genre> findAll(Pageable pageable) {
        return genreJpaRepository.findAll(pageable).map(genrePersistenceMapper::toDomain);
    }

    @Override
    public Page<Genre> findByStatus(GenreStatus status, Pageable pageable) {
        return genreJpaRepository.findByStatus(status, pageable).map(genrePersistenceMapper::toDomain);
    }

    @Override
    public Page<Genre> findByNameContainingAndStatus(String name, GenreStatus status, Pageable pageable) {
        return genreJpaRepository.findByNameContainingIgnoreCaseAndStatus(name, status, pageable).map(genrePersistenceMapper::toDomain);
    }

    @Override
    public Page<Genre> findByNameContaining(String name, Pageable pageable) {
        return genreJpaRepository.findByNameContainingIgnoreCase(name, pageable).map(genrePersistenceMapper::toDomain);
    }

    @Override
    @Transactional
    public void removeGenreFromAllMovies(String genreId) {
        genreJpaRepository.removeGenreFromAllMovies(genreId);
    }
}
