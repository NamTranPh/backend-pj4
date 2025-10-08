package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.domain.entities.HistoryWatching;

public interface HistoryWatchingRepository {
    // Basic CRUD
    HistoryWatching save(HistoryWatching history);
    Optional<HistoryWatching> findById(String historyId);
    List<HistoryWatching> findAll();
    void deleteById(String historyId);
    
    // User watching history
    List<HistoryWatching> findByUserId(String userId);
    List<HistoryWatching> findByUserIdOrderByWatchedAtDesc(String userId);
    Optional<HistoryWatching> findByUserIdAndMovieId(String userId, String movieId);
    Optional<HistoryWatching> findByUserIdAndMovieIdAndEpisodeId(String userId, String movieId, String episodeId);
    
    // Continue watching
    List<HistoryWatching> findByUserIdAndIsCompletedFalse(String userId);
    List<HistoryWatching> findByUserIdAndProgressBetween(String userId, Double minProgress, Double maxProgress);
    
    // Movie/Episode history
    List<HistoryWatching> findByMovieId(String movieId);
    List<HistoryWatching> findByEpisodeId(String episodeId);
    
    // Completed watching
    List<HistoryWatching> findByUserIdAndIsCompletedTrue(String userId);
    
    // Recent watching
    List<HistoryWatching> findRecentWatchingByUserId(String userId, int limit);
    
    // Statistics
    long countByMovieId(String movieId);
    long countByEpisodeId(String episodeId);
    long countByUserId(String userId);
    Double calculateAverageProgressByMovieId(String movieId);
}