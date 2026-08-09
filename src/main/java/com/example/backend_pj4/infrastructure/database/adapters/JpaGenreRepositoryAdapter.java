package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.domain.model.Genre;
import com.example.backend_pj4.domain.repository.GenreRepository;
import com.example.backend_pj4.infrastructure.database.mappers.GenrePersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.SpringDataGenreRepository;

@Repository
public class JpaGenreRepositoryAdapter implements GenreRepository {

    private final SpringDataGenreRepository SpringDataGenreRepository;
    private final GenrePersistenceMapper GenrePersistenceMapper;

    public JpaGenreRepositoryAdapter(SpringDataGenreRepository SpringDataGenreRepository, GenrePersistenceMapper GenrePersistenceMapper) {
        this.SpringDataGenreRepository = SpringDataGenreRepository;
        this.GenrePersistenceMapper = GenrePersistenceMapper;
    }

    @Override
    public Genre save(Genre genre) {
        return GenrePersistenceMapper.toDomain(SpringDataGenreRepository.save(GenrePersistenceMapper.toEntity(genre)));
    }

    @Override
    public Optional<Genre> findById(String id) {
        return SpringDataGenreRepository.findById(id).map(GenrePersistenceMapper::toDomain);
    }

    @Override
    public List<Genre> findAll() {
        return SpringDataGenreRepository.findAll().stream().map(GenrePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        SpringDataGenreRepository.deleteById(id);
    }

    @Override
    public Optional<Genre> findByName(String name) {
        return SpringDataGenreRepository.findByName(name).map(GenrePersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return SpringDataGenreRepository.existsByName(name);
    }

    @Override
    public List<Genre> findByNameContaining(String name) {
        return SpringDataGenreRepository.findByNameContainingIgnoreCase(name).stream().map(GenrePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Genre> findAllByIds(List<String> ids) {
        return SpringDataGenreRepository.findAllById(ids).stream().map(GenrePersistenceMapper::toDomain).collect(Collectors.toList());
    }
}


