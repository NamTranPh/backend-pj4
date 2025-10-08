package com.example.backend_pj4.infrastructure.databases.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.domain.entities.Genre;
import com.example.backend_pj4.domain.repository.GenreRepository;
import com.example.backend_pj4.infrastructure.databases.mapper.GenreMapper;
import com.example.backend_pj4.infrastructure.databases.repository.JpaGenreRepository;

@Repository
public class GenreRepositoryImpl implements GenreRepository {
    private final JpaGenreRepository jpaGenreRepository;

    public GenreRepositoryImpl(JpaGenreRepository jpaGenreRepository) {
        this.jpaGenreRepository = jpaGenreRepository;
    }

    @Override
    public Genre save(Genre genre) {
        var entity = GenreMapper.toEntity(genre);
        var savedEntity = jpaGenreRepository.save(entity);
        return GenreMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Genre> findById(String genreId) {
        return jpaGenreRepository.findById(genreId)
                .map(GenreMapper::toDomain);
    }

    @Override
    public List<Genre> findAll() {
        return jpaGenreRepository.findAll().stream()
                .map(GenreMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String GenreId) {
        jpaGenreRepository.deleteById(GenreId);
    }

    @Override
    public Optional<Genre> findByName(String name) {
        return jpaGenreRepository.findByName(name)
                .map(GenreMapper::toDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaGenreRepository.existsByName(name);
    }

    @Override
    public List<Genre> findByNameContaining(String keyword) {
        return jpaGenreRepository.findByNameContaining(keyword)
                .stream()
                .map(GenreMapper::toDomain)
                .collect(Collectors.toList());
    }
}
