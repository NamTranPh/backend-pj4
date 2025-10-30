package com.example.backend_pj4.infrastructure.databases.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.domain.entities.Episode;
import com.example.backend_pj4.domain.repository.EpisodeRepository;
import com.example.backend_pj4.infrastructure.databases.mapper.EpisodeMapper;
import com.example.backend_pj4.infrastructure.databases.repository.JpaEpisodeRepository;

@Repository
public class EpisodeRepositoryImpl implements EpisodeRepository {
    private final JpaEpisodeRepository jpaEpisodeRepository;
    private final EpisodeMapper episodeMapper;

    public EpisodeRepositoryImpl(JpaEpisodeRepository jpaEpisodeRepository, EpisodeMapper episodeMapper) {
        this.jpaEpisodeRepository = jpaEpisodeRepository;
        this.episodeMapper = episodeMapper;
    }

    @Override
    public Episode save(Episode episode) {
        var entity = episodeMapper.toEntity(episode);
        var savedEntity = jpaEpisodeRepository.save(entity);
        return episodeMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Episode> findById(String episodeId) {
        return jpaEpisodeRepository.findById(episodeId)
                .map(episodeMapper::toDomain);
    }

    @Override
    public List<Episode> findAll() {
        return jpaEpisodeRepository.findAll().stream()
                .map(episodeMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String episodeId) {
        jpaEpisodeRepository.deleteById(episodeId);
    }

    @Override
    public List<Episode> findByMovieId(String movieId) {
        return jpaEpisodeRepository.findByMovieId(movieId).stream()
                .map(episodeMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Episode> findByMovieIdAndEpisodeNumber(String movieId, Integer episodeNumber) {
        return jpaEpisodeRepository.findByMovieIdAndEpisodeNumber(movieId, episodeNumber)
                .map(episodeMapper::toDomain);
    }

    @Override
    public List<Episode> findByMovieIdOrderByEpisodeNumber(String movieId) {
        return jpaEpisodeRepository.findByMovieIdOrderByEpisodeNumber(movieId).stream()
                .map(episodeMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Episode> findPremiumEpisodesByMovieId(String movieId) {
        return jpaEpisodeRepository.findPremiumEpisodesByMovieId(movieId).stream()
                .map(episodeMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Episode> findFreeEpisodesByMovieId(String movieId) {
        return jpaEpisodeRepository.findFreeEpisodesByMovieId(movieId).stream()
                .map(episodeMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Episode> findActiveEpisodesByMovieId(String movieId) {
        return jpaEpisodeRepository.findActiveEpisodesByMovieId(movieId).stream()
                .map(episodeMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByMovieId(String movieId) {
        return jpaEpisodeRepository.countByMovieId(movieId);
    }

    @Override
    public long countActiveEpisodesByMovieId(String movieId) {
        return jpaEpisodeRepository.countActiveEpisodesByMovieId(movieId);
    }

    @Override
    public List<Episode> findLatestEpisodes(int limit) {
        return jpaEpisodeRepository.findLatestEpisodes(limit).stream()
                .map(episodeMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Episode> findByMovieIdAndEpisodeNumberGreaterThan(String movieId, Integer episodeNumber) {
        return jpaEpisodeRepository.findByMovieIdAndEpisodeNumberGreaterThan(movieId, episodeNumber).stream()
                .map(episodeMapper::toDomain)
                .collect(Collectors.toList());
    }
}
