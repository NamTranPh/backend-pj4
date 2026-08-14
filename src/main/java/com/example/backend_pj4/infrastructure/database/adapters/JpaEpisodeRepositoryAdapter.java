package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.common.constants.enums.VideoStatus;
import com.example.backend_pj4.domain.model.Episode;
import com.example.backend_pj4.domain.repository.EpisodeRepository;
import com.example.backend_pj4.infrastructure.database.mappers.EpisodePersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.EpisodeJpaRepository;

@Repository
public class JpaEpisodeRepositoryAdapter implements EpisodeRepository {

    private final EpisodeJpaRepository episodeJpaRepository;
    private final EpisodePersistenceMapper episodePersistenceMapper;

    public JpaEpisodeRepositoryAdapter(EpisodeJpaRepository episodeJpaRepository, EpisodePersistenceMapper episodePersistenceMapper) {
        this.episodeJpaRepository = episodeJpaRepository;
        this.episodePersistenceMapper = episodePersistenceMapper;
    }

    @Override
    public Episode save(Episode episode) {
        return episodePersistenceMapper.toDomain(episodeJpaRepository.save(episodePersistenceMapper.toEntity(episode)));
    }

    @Override
    public Optional<Episode> findById(String id) {
        return episodeJpaRepository.findById(id).map(episodePersistenceMapper::toDomain);
    }

    @Override
    public List<Episode> findAll() {
        return episodeJpaRepository.findAll().stream().map(episodePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        episodeJpaRepository.deleteById(id);
    }

    @Override
    public List<Episode> findByMovieId(String movieId) {
        return episodeJpaRepository.findByMovie_Id(movieId).stream().map(episodePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Episode> findByMovieIdAndEpisodeNumber(String movieId, Integer episodeNumber) {
        return episodeJpaRepository.findByMovie_IdAndEpisodeNumber(movieId, episodeNumber).map(episodePersistenceMapper::toDomain);
    }

    @Override
    public List<Episode> findByMovieIdOrderByEpisodeNumber(String movieId) {
        return episodeJpaRepository.findByMovie_IdOrderByEpisodeNumberAsc(movieId).stream().map(episodePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Episode> findByStatus(VideoStatus status) {
        return episodeJpaRepository.findByStatus(status).stream().map(episodePersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public long countByMovieId(String movieId) {
        return episodeJpaRepository.countByMovie_Id(movieId);
    }
}


