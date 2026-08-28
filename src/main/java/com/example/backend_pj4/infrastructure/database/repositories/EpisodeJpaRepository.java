package com.example.backend_pj4.infrastructure.database.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend_pj4.common.constants.enums.VideoStatus;
import com.example.backend_pj4.infrastructure.database.entities.EpisodeJpaEntity;

public interface EpisodeJpaRepository extends JpaRepository<EpisodeJpaEntity, String> {
    List<EpisodeJpaEntity> findByMovie_Id(String movieId);
    Optional<EpisodeJpaEntity> findByMovie_IdAndEpisodeNumber(String movieId, Integer episodeNumber);
    List<EpisodeJpaEntity> findByMovie_IdOrderByEpisodeNumberAsc(String movieId);
    List<EpisodeJpaEntity> findByStatus(VideoStatus status);
    long countByMovie_Id(String movieId);
    long countByMovie_IdAndStatus(String movieId, VideoStatus status);
}


