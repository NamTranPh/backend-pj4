package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.domain.entities.HistoryWatching;

public interface HistoryWatchingRepository {
    // Basic CRUD
    HistoryWatching save(HistoryWatching history);
    Optional<HistoryWatching> findById(Integer historyId);
    List<HistoryWatching> findAll();
    void deleteById(Integer historyId);
    
    // User watching history
    List<HistoryWatching> findByUserId(Integer userId);
    List<HistoryWatching> findByUserIdOrderByWatchedAtDesc(Integer userId);
    Optional<HistoryWatching> findByUserIdAndMovieId(Integer userId, Integer movieId);
    Optional<HistoryWatching> findByUserIdAndMovieIdAndEpisodeId(Integer userId, Integer movieId, Integer episodeId);
    
    // Continue watching
    List<HistoryWatching> findByUserIdAndIsCompletedFalse(Integer userId);
    List<HistoryWatching> findByUserIdAndProgressBetween(Integer userId, Double minProgress, Double maxProgress);
    
    // Movie/Episode history
    List<HistoryWatching> findByMovieId(Integer movieId);
    List<HistoryWatching> findByEpisodeId(Integer episodeId);
    
    // Completed watching
    List<HistoryWatching> findByUserIdAndIsCompletedTrue(Integer userId);
    
    // Recent watching
    List<HistoryWatching> findRecentWatchingByUserId(Integer userId, int limit);
    
    // Statistics
    long countByMovieId(Integer movieId);
    long countByEpisodeId(Integer episodeId);
    long countByUserId(Integer userId);
    Double calculateAverageProgressByMovieId(Integer movieId);
}