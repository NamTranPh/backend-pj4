package com.example.backend_pj4.infrastructure.databases.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.domain.entities.Genre;
import com.example.backend_pj4.domain.repository.GenreRepository;
import com.example.backend_pj4.infrastructure.databases.mapper.GenreMapper;
import com.example.backend_pj4.infrastructure.databases.mapper.MovieMapper;
import com.example.backend_pj4.infrastructure.databases.repository.JpaGenreRepository;

@Repository
public class GenreRepositoryImpl implements GenreRepository {
    private final JpaGenreRepository jpaGenreRepository;
    private final GenreMapper genreMapper;

    public GenreRepositoryImpl(JpaGenreRepository jpaGenreRepository, GenreMapper genreMapper) {
        this.jpaGenreRepository = jpaGenreRepository;
        this.genreMapper = genreMapper;
    }

    @Override
    public Genre save(Genre genre) {
        var entity = genreMapper.toEntity(genre);
        var savedEntity = jpaGenreRepository.save(entity);
        return genreMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Genre> findById(String genreId) {
        return jpaGenreRepository.findById(genreId)
                .map(genreMapper::toDomain);
    }

    @Override
    public List<Genre> findAll() {
        return jpaGenreRepository.findAll().stream()
                .map(genreMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String GenreId) {
        jpaGenreRepository.deleteById(GenreId);
    }

    @Override
    public Optional<Genre> findByName(String name) {
        return jpaGenreRepository.findByName(name)
                .map(genreMapper::toDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaGenreRepository.existsByName(name);
    }

    @Override
    public List<Genre> findByNameContaining(String keyword) {
        return jpaGenreRepository.findByNameContaining(keyword)
                .stream()
                .map(genreMapper::toDomain)
                .collect(Collectors.toList());
    }
}
