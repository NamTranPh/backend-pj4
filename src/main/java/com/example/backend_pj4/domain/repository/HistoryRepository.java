package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.domain.entities.HistoryWatching;

public interface HistoryRepository {
    HistoryWatching save(HistoryWatching history);
    Optional<HistoryWatching> findById(String id);
    List<HistoryWatching> findAll();
    void deleteById(String id);
    boolean existsById(String id);
    Optional<HistoryWatching> findByUserAndMovieAndEpisode(String userId, String movieId, String episodeId);
    Optional<HistoryWatching> findByUserAndMovie(String userId, String movieId);
    List<HistoryWatching> findByUserId(String userId);
    List<HistoryWatching> findTop10ByUserId(String userId);
    boolean existsByUserAndMovieAndEpisode(String userId, String movieId, String episodeId);
    void deleteByUserId(String userId);
    long countByUserId(String userId);
}
