package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.domain.model.HistoryWatching;
import com.example.backend_pj4.domain.repository.HistoryWatchingRepository;
import com.example.backend_pj4.infrastructure.database.mappers.HistoryWatchingPersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.HistoryWatchingJpaRepository;

@Repository
public class JpaHistoryWatchingRepositoryAdapter implements HistoryWatchingRepository {

    private final HistoryWatchingJpaRepository jpaRepository;
    private final HistoryWatchingPersistenceMapper mapper;

    public JpaHistoryWatchingRepositoryAdapter(HistoryWatchingJpaRepository jpaRepository, HistoryWatchingPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public HistoryWatching save(HistoryWatching history) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(history)));
    }

    @Override
    public Optional<HistoryWatching> findById(String id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<HistoryWatching> findByUserId(String userId) {
        return jpaRepository.findByUser_Id(userId).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<HistoryWatching> findByUserIdAndMovieId(String userId, String movieId) {
        return jpaRepository.findByUser_IdAndMovie_Id(userId, movieId).map(mapper::toDomain);
    }

    @Override
    public Optional<HistoryWatching> findByUserIdAndMovieIdAndEpisodeIsNull(String userId, String movieId) {
        return jpaRepository.findByUser_IdAndMovie_IdAndEpisodeIsNull(userId, movieId).map(mapper::toDomain);
    }

    @Override
    public Optional<HistoryWatching> findByUserIdAndMovieIdAndEpisodeId(String userId, String movieId, String episodeId) {
        return jpaRepository.findByUser_IdAndMovie_IdAndEpisode_Id(userId, movieId, episodeId).map(mapper::toDomain);
    }

    @Override
    public List<HistoryWatching> findTop10ByUserId(String userId) {
        return jpaRepository.findTop10ByUser_IdOrderByWatchedAtDesc(userId).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public long countByUserId(String userId) {
        return jpaRepository.countByUser_Id(userId);
    }

    @Override
    @Transactional
    public void deleteByUserId(String userId) {
        jpaRepository.deleteByUserId(userId);
    }
}


