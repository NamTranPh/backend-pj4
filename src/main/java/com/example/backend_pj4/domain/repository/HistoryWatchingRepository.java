package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.domain.model.HistoryWatching;

public interface HistoryWatchingRepository {
    HistoryWatching save(HistoryWatching history);
    Optional<HistoryWatching> findById(String id);
    void deleteById(String id);
    List<HistoryWatching> findByUserId(String userId);
    Optional<HistoryWatching> findByUserIdAndMovieId(String userId, String movieId);
    Optional<HistoryWatching> findByUserIdAndMovieIdAndEpisodeId(String userId, String movieId, String episodeId);
    List<HistoryWatching> findTop10ByUserId(String userId);
    long countByUserId(String userId);
    void deleteByUserId(String userId);
}
