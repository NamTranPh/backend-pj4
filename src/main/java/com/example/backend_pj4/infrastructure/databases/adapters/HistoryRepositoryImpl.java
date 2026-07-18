package com.example.backend_pj4.infrastructure.databases.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.domain.entities.HistoryWatching;
import com.example.backend_pj4.domain.repository.HistoryRepository;
import com.example.backend_pj4.infrastructure.databases.mapper.HistoryWatchingMapper;
import com.example.backend_pj4.infrastructure.databases.repository.JpaHistoryRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class HistoryRepositoryImpl implements HistoryRepository {

    private final JpaHistoryRepository jpaHistoryRepository;
    private final HistoryWatchingMapper historyMapper;

    @Override
    public HistoryWatching save(HistoryWatching history) {
        var entity = historyMapper.toEntity(history);
        var saved = jpaHistoryRepository.save(entity);
        return historyMapper.toDomain(saved);
    }

    @Override
    public Optional<HistoryWatching> findById(String id) {
        return jpaHistoryRepository.findById(id)
                .map(historyMapper::toDomain);
    }

    @Override
    public List<HistoryWatching> findAll() {
        return jpaHistoryRepository.findAll().stream()
                .map(historyMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        jpaHistoryRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return jpaHistoryRepository.existsById(id);
    }

    @Override
    public Optional<HistoryWatching> findByUserAndMovieAndEpisode(String userId, String movieId, String episodeId) {
        return jpaHistoryRepository.findByUserUserIdAndMovieMovieIdAndEpisodeEpisodeId(userId, movieId, episodeId)
                .map(historyMapper::toDomain);
    }

    @Override
    public Optional<HistoryWatching> findByUserAndMovie(String userId, String movieId) {
        return jpaHistoryRepository.findByUserUserIdAndMovieMovieIdAndEpisodeIsNull(userId, movieId)
                .map(historyMapper::toDomain);
    }

    @Override
    public List<HistoryWatching> findByUserId(String userId) {
        return jpaHistoryRepository.findByUserUserIdOrderByWatchedAtDesc(userId).stream()
                .map(historyMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<HistoryWatching> findTop10ByUserId(String userId) {
        return jpaHistoryRepository.findByUserUserIdOrderByWatchedAtDesc(userId).stream()
                .limit(10)
                .map(historyMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByUserAndMovieAndEpisode(String userId, String movieId, String episodeId) {
        return jpaHistoryRepository.findByUserUserIdAndMovieMovieIdAndEpisodeEpisodeId(userId, movieId, episodeId).isPresent();
    }

    @Override
    public void deleteByUserId(String userId) {
        jpaHistoryRepository.deleteByUserUserId(userId);
    }

    @Override
    public long countByUserId(String userId) {
        return jpaHistoryRepository.countByUserUserId(userId);
    }
}
