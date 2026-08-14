package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

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
}


