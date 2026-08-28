package com.example.backend_pj4.infrastructure.database.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend_pj4.infrastructure.database.entities.HistoryWatchingJpaEntity;

public interface HistoryWatchingJpaRepository extends JpaRepository<HistoryWatchingJpaEntity, String> {
    List<HistoryWatchingJpaEntity> findByUser_Id(String userId);
    Optional<HistoryWatchingJpaEntity> findByUser_IdAndMovie_Id(String userId, String movieId);
    Optional<HistoryWatchingJpaEntity> findByUser_IdAndMovie_IdAndEpisodeIsNull(String userId, String movieId);
    Optional<HistoryWatchingJpaEntity> findByUser_IdAndMovie_IdAndEpisode_Id(String userId, String movieId, String episodeId);
    List<HistoryWatchingJpaEntity> findTop10ByUser_IdOrderByWatchedAtDesc(String userId);
    long countByUser_Id(String userId);
    @Modifying
    @Query("DELETE FROM HistoryWatchingJpaEntity h WHERE h.user.id = :userId")
    void deleteByUserId(@Param("userId") String userId);
}


