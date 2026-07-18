package com.example.backend_pj4.infrastructure.databases.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.backend_pj4.infrastructure.databases.entities.HistoryWatchingEntity;

@Repository
public interface JpaHistoryRepository extends JpaRepository<HistoryWatchingEntity, String> {
    
    List<HistoryWatchingEntity> findByUserUserIdOrderByWatchedAtDesc(String userId);
    
    Optional<HistoryWatchingEntity> findByUserUserIdAndMovieMovieIdAndEpisodeEpisodeId(String userId, String movieId, String episodeId);
    
    Optional<HistoryWatchingEntity> findByUserUserIdAndMovieMovieIdAndEpisodeIsNull(String userId, String movieId);
    
    void deleteByUserUserId(String userId);
    
    void deleteByUserUserIdAndMovieMovieId(String userId, String movieId);
    
    long countByUserUserId(String userId);
    
    long countByMovieMovieId(String movieId);
    
    @Query("SELECT h FROM HistoryWatchingEntity h WHERE h.user.userId = ?1 ORDER BY h.watchedAt DESC")
    Page<HistoryWatchingEntity> findByUserUserIdPaged(String userId, Pageable pageable);
    
    @Query("SELECT h FROM HistoryWatchingEntity h WHERE h.user.userId = ?1 AND h.isCompleted = false ORDER BY h.watchedAt DESC")
    List<HistoryWatchingEntity> findContinueWatching(String userId);
    
    @Query("SELECT h FROM HistoryWatchingEntity h WHERE h.user.userId = ?1 AND h.isCompleted = true ORDER BY h.watchedAt DESC")
    List<HistoryWatchingEntity> findCompleted(String userId);
}
